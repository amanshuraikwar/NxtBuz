package io.github.amanshuraikwar.nxtbuz.data.busarrival.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.app.NotificationManagerCompat
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.busarrival.util.getArrivalTimeStrNotification
import io.github.amanshuraikwar.nxtbuz.data.room.busarrival.BusArrivalDao
import io.github.amanshuraikwar.nxtbuz.data.room.busarrival.BusArrivalStatus
import io.github.amanshuraikwar.nxtbuz.data.room.busstops.BusStopDao
import kotlinx.coroutines.withContext
import java.lang.Integer.min
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BusArrivalNotificationManager @Inject constructor(
    private val context: Context,
    private val busArrivalDao: BusArrivalDao,
    private val busStopDao: BusStopDao,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    private val busStopCodeNotificationIdMap = ConcurrentHashMap<String, Int>()

    suspend fun showNotification(busStopCode: String) = withContext(dispatcherProvider.io) {

        val busArrivalEntityList = busArrivalDao.findByBusStopCode(busStopCode)

        if (busArrivalEntityList.isEmpty()) {
            Log.w(
                TAG,
                "showNotification: Bus arrival entity list " +
                        "for bus stop code $busStopCode is empty."
            )
            return@withContext
        }

        val busStopDescription =
            busStopDao.findByCode(busStopCode).firstOrNull()?.description
                ?: run {
                    throw Exception("No bus stop found for code $busStopCode.")
                }

        val arrivalStr = busArrivalEntityList
            .groupBy { it.busServiceNumber }
            .filter { (_, busArrivalEntityList) ->
                busArrivalEntityList[0].busArrivalStatus == BusArrivalStatus.ARRIVING
            }
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

        createNotificationChannel()

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_noti_bus_arrival)
            .setContentTitle("Arriving at $busStopDescription")
            .setContentText(arrivalStr.split("\n")[0]+"...")
            .setSubText(busStopDescription)
            .setStyle(NotificationCompat.BigTextStyle().bigText(arrivalStr).setSummaryText(busStopCode))
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setVisibility(VISIBILITY_PUBLIC)
            .setVibrate(null)
            .setOngoing(true)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification
            notify(
                busStopCodeNotificationIdMap[busStopCode]
                    ?: run {
                        val newId = busStopCodeNotificationIdMap.size + 1
                        busStopCodeNotificationIdMap[busStopCode] = newId
                        newId
                    },
                builder.build()
            )
        }
    }

    fun cancel(busStopCode: String) {
        busStopCodeNotificationIdMap[busStopCode]?.let { notificationId ->
            with(NotificationManagerCompat.from(context)) {
                cancel(notificationId)
            }
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name_bus_arrival)
            val descriptionText = context.getString(R.string.channel_description_bus_arrival)
            val importance = NotificationManager.IMPORTANCE_MIN
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val TAG = "BusArrivalNotificationM"
        private const val CHANNEL_ID = "CHANNEL_BUS_ARRIVAL"
    }
}