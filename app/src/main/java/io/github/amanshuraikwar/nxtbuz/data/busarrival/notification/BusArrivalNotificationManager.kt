package io.github.amanshuraikwar.nxtbuz.data.busarrival.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.busarrival.util.asArrivingInMin
import io.github.amanshuraikwar.nxtbuz.data.busarrival.util.asArrivingInMinutesStr
import io.github.amanshuraikwar.nxtbuz.data.busarrival.util.getArrivalTimeStrNotification
import io.github.amanshuraikwar.nxtbuz.data.busarrival.util.isArrivingIn
import io.github.amanshuraikwar.nxtbuz.data.prefs.PreferenceStorage
import io.github.amanshuraikwar.nxtbuz.data.prefs.model.AlertFrequency
import io.github.amanshuraikwar.nxtbuz.data.room.busarrival.BusArrivalDao
import io.github.amanshuraikwar.nxtbuz.data.room.busarrival.BusArrivalEntity
import io.github.amanshuraikwar.nxtbuz.data.room.busarrival.BusArrivalStatus
import io.github.amanshuraikwar.nxtbuz.data.room.busstops.BusStopDao
import io.github.amanshuraikwar.nxtbuz.data.room.starredbusstops.StarredBusStopsDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
class BusArrivalNotificationManager @Inject constructor(
    private val context: Context,
    private val busArrivalDao: BusArrivalDao,
    private val starredBusArrivalDao: StarredBusStopsDao,
    private val busStopDao: BusStopDao,
    private val preferenceStorage: PreferenceStorage,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) {

    private val busStopCodeNotificationIdMap = ConcurrentHashMap<String, Int>()

    // map of
    // busServiceNumber>busStopCode -> (NotificationId, ArrivalDateTime)
    @Deprecated("")
    private val starredBusArrivalNotificationIdMap =
        ConcurrentHashMap<String, StarredBusArrivalNotification>()

    private val starredBusArrivalNotificationStore = StarredBusArrivalNotificationStore()

    suspend fun createNotification(
        busStopCode: String
    ): Pair<Int, Notification> = withContext(dispatcherProvider.io) {

        createBusArrivalNotificationChannel()

        var busArrivalEntityList = busArrivalDao.findByBusStopCode(busStopCode)

        // fail fast if there are no bus arrival entities in DB
        // we are throwing exception here because calling this function
        // expects bus arrival entities to be already fetched
        if (busArrivalEntityList.isEmpty()) {
            throw IllegalArgumentException(
                "Bus arrival entity list " +
                        "for bus stop code $busStopCode is empty."
            )
        }

        // fail fast if we cannot find bus stop description
        val busStopDescription =
            busStopDao.findByCode(busStopCode).firstOrNull()?.description
                ?: run {
                    throw IllegalArgumentException("No bus stop found for code $busStopCode.")
                }

        // take only those entities which are arriving
        busArrivalEntityList = busArrivalEntityList.filter {
            it.busArrivalStatus == BusArrivalStatus.ARRIVING
        }

        // if no buses are arriving
        // show appropriate notification and return
        if (busArrivalEntityList.isEmpty()) {
            return@withContext createNoArrivingBusNotification(busStopCode, busStopDescription)
        }

        val busArrivalStr = busArrivalEntityList.toBusArrivalString()

        val notificationIdNotificationPair = createArrivingBusNotification(
            busStopCode,
            busStopDescription,
            busArrivalStr
        )

        alertStarredArrivals(busStopCode, busStopDescription, busArrivalEntityList)

        return@withContext notificationIdNotificationPair
    }

    private fun createArrivingBusNotification(
        busStopCode: String,
        busStopDescription: String,
        busArrivalStr: String,
    ): Pair<Int, Notification> {

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_BUS_ARRIVAL)
            .setSmallIcon(R.drawable.ic_noti_bus_arrival)
            .setContentTitle("Buses arriving at $busStopDescription")
            //.setContentText(arrivalStr.split("\n")[0] + "...")
            .setContentText(busArrivalStr)
            .setSubText(busStopDescription)
            .setStyle(
                NotificationCompat
                    .BigTextStyle()
                    .bigText(busArrivalStr)
                    .setSummaryText(busStopDescription)
            )
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setVisibility(VISIBILITY_PUBLIC)
            .setVibrate(null)
            .setOngoing(true)
            .build()

        val id = getNotificationId(busStopCode)

        return Pair(id, notification)
    }

    private fun List<BusArrivalEntity>.toBusArrivalString(): String {
        return groupBy { it.busServiceNumber }
            .map { (busServiceNumber, busArrivalEntityList) ->
                busArrivalEntityList
                    .sortedBy { it.seqNumber }
                    .subList(0, 2.coerceAtMost(busArrivalEntityList.size))
                    .fold(
                        "$busServiceNumber arriving ",
                        { acc, busArrivalEntity ->
                            val str = busArrivalEntity.getArrivalTimeStrNotification()
                            if (str.isNotBlank()) {
                                "$acc${busArrivalEntity.getArrivalTimeStrNotification()} and "
                            } else {
                                acc
                            }
                        }
                    )
                    .dropLast(5)
            }
            .foldRight(
                "",
                { str, acc -> "$acc\n$str" }
            )
            .drop(1)
    }

    private suspend inline fun alertStarredArrivals(
        busStopCode: String,
        busStopDescription: String,
        busArrivalEntityList: List<BusArrivalEntity>,
    ) {

        // if disabled in settings
        // clear the notification id cache and return
        if (!preferenceStorage.alertStarredBusArrivals) {
            starredBusArrivalNotificationStore.clear()
            return
        }

        // in minutes
        val alertTimeLimit = preferenceStorage.alertStarredBusArrivalsMinutes

        createStarredBusArrivalNotificationChannel()

        // get starred buses for the bus stop
        val starredBusServiceNumberSet =
            starredBusArrivalDao.findByBusStopCode(busStopCode).map { it.busServiceNumber }.toSet()

        // busServiceNumber -> arrival timestamp
        val arrivingBusServiceNumberTimeStampMap = busArrivalEntityList
            .groupBy { it.busServiceNumber }
            .filter { (busServiceNumber, _) ->
                // take only buses which are starred
                starredBusServiceNumberSet.contains(busServiceNumber)
            }
            .mapValues { (busServiceNumber, busArrivalEntityList) ->
                // take only bus arrival with sequence number 1
                // i.e. first arriving bus
                busArrivalEntityList.find { it.seqNumber == 1 } ?: throw IllegalArgumentException(
                    "Couldn't find bus arrival entity with sequence number 1 for " +
                            "bus service $busServiceNumber at bus stop $busStopCode."
                )
            }
            .filter { (_, busArrivalEntity) ->
                // take only buses which are arriving in alert time limit
                busArrivalEntity.isArrivingIn(alertTimeLimit)
            }
            .mapValues { (_, busArrivalEntity) ->
                // only take estimated arrival time stamp
                busArrivalEntity.estimatedArrivalTimestamp
            }

        val arrivingStarredBusServiceNumberSet =
            arrivingBusServiceNumberTimeStampMap.keys

        // remove notification for not arriving buses
        starredBusArrivalNotificationStore.minus(
            busStopCode, arrivingStarredBusServiceNumberSet)
            .forEach { (busServiceNumber, starredBusArrivalNotification) ->
                val (notificationId, _) = starredBusArrivalNotification
                with(NotificationManagerCompat.from(context)) {
                    cancel(notificationId)
                }
                starredBusArrivalNotificationStore.remove(busStopCode, busServiceNumber)
            }

        // number of existing notifications which were updated
        // +
        // number of new added notifications
        //
        // does not include bus arrival notifications which were canceled
        //
        // used further to decide whether we should update/show group notifications
        var updatedNotificationCount = 0

        var groupNotificationContentText: String? = null

        arrivingBusServiceNumberTimeStampMap
            .forEach { (busServiceNumber, arrivalTimeStamp) ->

                starredBusArrivalNotificationStore.getNotification(busStopCode, busServiceNumber)
                    ?.let { lastNotification ->

                        if (lastNotification.shouldShowAgain(arrivalTimeStamp)) {

                            val notification = lastNotification.copy(
                                arrivalTimeStamp = arrivalTimeStamp,
                                arrivingInMin = arrivalTimeStamp.asArrivingInMin()
                            )

                            starredBusArrivalNotificationStore.putNotification(
                                busStopCode,
                                busServiceNumber,
                                notification
                            )

                            showStarredBusNotification(
                                busServiceNumber,
                                busStopDescription,
                                notification,
                            ).let {
                                if (groupNotificationContentText == null) {
                                    groupNotificationContentText = it
                                }
                            }

                            updatedNotificationCount++
                        }
                    }
                    ?: run {

                        val notification = StarredBusArrivalNotification(
                            starredBusArrivalNotificationStore.createNewNotificationId(),
                            arrivalTimeStamp,
                            arrivalTimeStamp.asArrivingInMin()
                        )

                        starredBusArrivalNotificationStore.putNotification(
                            busStopCode,
                            busServiceNumber,
                            notification
                        )

                        showStarredBusNotification(
                            busServiceNumber,
                            busStopDescription,
                            notification
                        ).let {
                            if (groupNotificationContentText == null) {
                                groupNotificationContentText = it
                            }
                        }

                        updatedNotificationCount++
                    }
            }

        val arrivingBusCount = arrivingStarredBusServiceNumberSet.size

        // if no buses are arriving
        // cancel the group notification
        if (arrivingBusCount == 0) {

            with(NotificationManagerCompat.from(context)) {
                cancel(GROUP_ID_STARRED_BUS_ARRIVAL)
            }

        }

        // if at least one notification was changed
        // updated the group notification
        if (updatedNotificationCount != 0) {

            val star = context.getString(R.string.star)

            val notification = NotificationCompat
                .Builder(context, CHANNEL_ID_STARRED_BUS_ARRIVAL)
                .setContentTitle("$arrivingBusCount starred buses are arriving now at $busStopDescription")
                //set content text to support devices running API level < 24
                .setContentText(groupNotificationContentText)
                .setSmallIcon(R.drawable.ic_noti_bus_arrival)
                .setSubText("$busStopDescription ($star)")
                .setGroup(GROUP_KEY_STARRED_BUS_ARRIVAL)
                .setGroupSummary(true)
                .build()

            NotificationManagerCompat.from(context).apply {
                notify(GROUP_ID_STARRED_BUS_ARRIVAL, notification)
            }
        }
    }

    private fun StarredBusArrivalNotification.shouldShowAgain(
        newArrivalTimeStamp: OffsetDateTime
    ): Boolean {
        return when (preferenceStorage.alertStarredBusArrivalsFrequency) {
            AlertFrequency.ONCE -> {
                newArrivalTimeStamp != arrivalTimeStamp
            }
            AlertFrequency.EVERY_TIME_BUS_GETS_CLOSER -> {
                newArrivalTimeStamp != arrivalTimeStamp
                        || arrivingInMin != newArrivalTimeStamp.asArrivingInMin()
            }
        }
    }

    /**
     * @return Notification content title.
     */
    private fun showStarredBusNotification(
        busServiceNumber: String,
        busStopDescription: String,
        starredBusArrivalNotification: StarredBusArrivalNotification,
    ): String {

        val contentTitle = "$busServiceNumber is arriving " +
                "${starredBusArrivalNotification.arrivalTimeStamp.asArrivingInMinutesStr()} " +
                "at $busStopDescription"

        val notification = NotificationCompat
            .Builder(context, CHANNEL_ID_STARRED_BUS_ARRIVAL)
            .setSmallIcon(R.drawable.ic_noti_bus_arrival)
            .setContentTitle(contentTitle)
            .setSubText("$busServiceNumber at $busStopDescription")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(VISIBILITY_PUBLIC)
            .setGroup(GROUP_KEY_STARRED_BUS_ARRIVAL)
            .build()

        NotificationManagerCompat.from(context).apply {
            notify(starredBusArrivalNotification.notificationId, notification)
        }

        return contentTitle
    }

    private fun createNoArrivingBusNotification(
        busStopCode: String,
        busStopDescription: String,
    ): Pair<Int, Notification> {

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_BUS_ARRIVAL)
            .setSmallIcon(R.drawable.ic_noti_bus_arrival)
            .setContentTitle("Arriving at $busStopDescription")
            .setContentText("No buses are arriving.")
            .setSubText(busStopDescription)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setVisibility(VISIBILITY_PUBLIC)
            .setVibrate(null)
            .setOngoing(true)

        val id = getNotificationId(busStopCode)

        val notification = builder.build()

        return Pair(id, notification)
    }

    fun cancel(busStopCode: String) {
        busStopCodeNotificationIdMap[busStopCode]?.let { notificationId ->
            with(NotificationManagerCompat.from(context)) {
                cancel(notificationId)
            }
        }
    }

    private fun createStarredBusArrivalNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name_starred_bus_arrival)
            val descriptionText =
                context.getString(R.string.channel_description_starred_bus_arrival)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel =
                NotificationChannel(CHANNEL_ID_STARRED_BUS_ARRIVAL, name, importance).apply {
                    description = descriptionText
                }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createBusArrivalNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name_bus_arrival)
            val descriptionText = context.getString(R.string.channel_description_bus_arrival)
            val importance = NotificationManager.IMPORTANCE_MIN
            val channel =
                NotificationChannel(CHANNEL_ID_BUS_ARRIVAL, name, importance).apply {
                    description = descriptionText
                }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @Synchronized
    private fun getNotificationId(busStopCode: String): Int {
        return busStopCodeNotificationIdMap[busStopCode]
            ?: run {
                val newId = busStopCodeNotificationIdMap.size + 1
                busStopCodeNotificationIdMap[busStopCode] = newId
                newId
            }
    }

    companion object {
        private const val CHANNEL_ID_BUS_ARRIVAL = "CHANNEL_BUS_ARRIVAL"

        private const val GROUP_ID_STARRED_BUS_ARRIVAL = 101
        private const val GROUP_KEY_STARRED_BUS_ARRIVAL =
            "io.github.amanshuraikwar.nxtbuz.data.busarrival.notification.STARRED_BUS_ARRIVAL"
        private const val CHANNEL_ID_STARRED_BUS_ARRIVAL = "CHANNEL_STARRED_BUS_ARRIVAL"
    }
}