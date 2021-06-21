package io.github.amanshuraikwar.nxtbuz.data.busarrival.service

//import android.app.Service
//import android.content.Intent
//import android.os.IBinder
//import android.util.Log
//import androidx.appcompat.app.AppCompatActivity
//import com.google.firebase.crashlytics.FirebaseCrashlytics
//import dagger.android.DaggerService
//import io.github.amanshuraikwar.ltaapi.LtaApi
//import io.github.amanshuraikwar.ltaapi.model.BusArrivalItemDto
//import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
////import io.github.amanshuraikwar.nxtbuz.common.model.BusArrivalsState
//import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusLoad
//import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusType
//import io.github.amanshuraikwar.nxtbuz.data.busarrival.notification.BusArrivalNotificationManager
//import io.github.amanshuraikwar.nxtbuz.data.room.dao.BusArrivalDao
//import io.github.amanshuraikwar.nxtbuz.common.model.room.BusArrivalEntity
//import io.github.amanshuraikwar.nxtbuz.data.room.dao.BusOperatorDao
//import io.github.amanshuraikwar.nxtbuz.common.model.room.BusOperatorEntity
//import io.github.amanshuraikwar.nxtbuz.data.room.dao.OperatingBusDao
//import io.github.amanshuraikwar.nxtbuz.common.util.TimeUtil
//import kotlinx.coroutines.*
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.flowOf
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.flow.toCollection
//import org.threeten.bp.OffsetDateTime
//import org.threeten.bp.OffsetTime
//import javax.inject.Inject
//import javax.inject.Named

//class BusArrivalService : DaggerService() {
//
//    @Inject
//    lateinit var dispatcherProvider: CoroutinesDispatcherProvider
//
//    @Inject
//    lateinit var busApi: LtaApi
//
//    @Inject
//    lateinit var busOperatorDao: BusOperatorDao
//
//    @Inject
//    lateinit var operatingBusDao: OperatingBusDao
//
//    @Inject
//    lateinit var busArrivalDao: BusArrivalDao
//
//    /**
//     * @see https://medium.com/@WindRider/correct-usage-of-dagger-2-named-annotation-in-kotlin-8ab17ced6928
//     */
//    @Suppress("KDocUnresolvedReference")
//    @field:[Inject Named("busArrivalStateFlow")]
//    lateinit var busArrivalEntityStateFlow: MutableStateFlow<BusArrivalsState>
//
//    @Inject
//    lateinit var busArrivalNotificationManager: BusArrivalNotificationManager
//
//    private val coroutineScope: CoroutineScope by lazy {
//        // We use supervisor scope because we don't want
//        // the child coroutines to cancel all the parent coroutines
//        CoroutineScope(SupervisorJob() + dispatcherProvider.arrivalService)
//    }
//
//    private var currentCoroutineJob: Job? = null
//    private var currentBusStopCode: String = ""
//    private var showNotification: Boolean = false
//
//    private val busArrivalServiceLoopErrorHandler = CoroutineExceptionHandler { _, throwable ->
//        Log.e(TAG, "busArrivalServiceLoopErrorHandler: ${throwable.message}", throwable)
//        if (throwable !is CancellationException) {
//            startBusArrivalLoopDelayed()
//        }
//    }
//
//    override fun onBind(intent: Intent?): IBinder? {
//        return null
//    }
//
//    @Synchronized
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//
//        val busStopCode = intent?.getStringExtra(KEY_BUS_STOP_CODE) ?: return START_TYPE
//
//        showNotification = intent.getBooleanExtra(KEY_SHOW_NOTIFICATION, false)
//
//        if (busStopCode == currentBusStopCode) return START_TYPE
//
//        currentBusStopCode = ""
//        currentCoroutineJob?.cancel()
//
//        currentBusStopCode = busStopCode
//        currentCoroutineJob = startBusArrivalLoop()
//
//        return START_TYPE
//    }
//
//    @Synchronized
//    private fun startBusArrivalLoop(): Job {
//        return arrivalLoop(currentBusStopCode)
//    }
//
//    @Synchronized
//    private fun startBusArrivalLoopDelayed(): Job {
//        return arrivalLoop(currentBusStopCode, DELAY_TIME_MILLIS)
//    }
//
//    private fun arrivalLoop(
//        busStopCode: String,
//        initialDelay: Long = 0L
//    ) = coroutineScope.launch(busArrivalServiceLoopErrorHandler) {
//
//        delay(initialDelay)
//
//        val operatingBusServiceNumberSet =
//            operatingBusDao
//                .findByBusStopCode(busStopCode)
//                .map { it.busServiceNumber }
//                .toSet()
//
//        while (isActive) {
//
//            val busArrivalResponse = busApi.getBusArrivals(busStopCode)
//
//            val operatingBusServiceNumberMutableSet =
//                operatingBusServiceNumberSet.toMutableSet()
//
//            busArrivalResponse.busArrivals.forEach { busArrivalItem ->
//
//                // if bus service number was not found in local DB
//                // report to crashlytics
//                if (!operatingBusServiceNumberMutableSet.remove(busArrivalItem.serviceNumber)) {
//                    FirebaseCrashlytics
//                        .getInstance()
//                        .recordException(
//                            Exception(
//                                "No operating bus row found for service number " +
//                                        "${busArrivalItem.serviceNumber} and stop code " +
//                                        "$busStopCode in local DB."
//                            )
//                        )
//                }
//
//                updateBusOperator(
//                    busStopCode = busStopCode,
//                    busServiceNumber = busArrivalItem.serviceNumber,
//                    operator = busArrivalItem.operator
//                )
//
//                val busArrivalEntityList = busArrivalItem.asBusArrivalEntityList(busStopCode)
//
//                updateBusArrivals(
//                    busStopCode,
//                    busServiceNumber = busArrivalItem.serviceNumber,
//                    busArrivalEntityList
//                )
//            }
//
//            // for the operating buses which were not fetched from remote
//            // update bus arrival entities with error
//
//            operatingBusServiceNumberMutableSet.forEach { busServiceNumber ->
//
//                updateBusArrivals(
//                    busStopCode,
//                    busServiceNumber,
//                    mutableListOf<BusArrivalEntity>().apply {
//                        flowOf(1, 2, 3)
//                            .map { seq ->
//                                getErrorBusArrivalEntity(
//                                    busStopCode = busStopCode,
//                                    busServiceNumber = busServiceNumber,
//                                    seq
//                                )
//                            }
//                            .toCollection(this)
//                    }
//                )
//            }
//
//            busArrivalNotificationManager
//                .createNotification(busStopCode)
//                .let { (id, notification) ->
//                    startForeground(id, notification)
//                }
//
//            if (isActive) emit(busStopCode)
//
//            delay(DELAY_TIME_MILLIS)
//        }
//    }
//
//    private suspend inline fun emit(busStopCode: String) {
//        busArrivalEntityStateFlow.value = BusArrivalsState(
//            busStopCode,
//            busArrivalDao.findByBusStopCode(busStopCode)
//        )
//    }
//
//    private suspend inline fun updateBusArrivals(
//        busStopCode: String,
//        busServiceNumber: String,
//        busArrivalEntityList: List<BusArrivalEntity>
//    ) {
//
//        if (busArrivalDao
//                .findByBusServiceNumberAndBusStopCode(busServiceNumber, busStopCode)
//                .isEmpty()
//        ) {
//
//            busArrivalDao.insertAll(busArrivalEntityList)
//
//        } else {
//            busArrivalDao.updateAll(busArrivalEntityList)
//        }
//    }
//
//    private suspend inline fun BusArrivalItemDto.asBusArrivalEntityList(
//        busStopCode: String
//    ): List<BusArrivalEntity> {
//
//        val busArrivalEntityList = mutableListOf<BusArrivalEntity>()
//        var seq = 0
//
//        val arrivingBusNum: Int
//
//        listOf(arrivingBus, arrivingBus1, arrivingBus2)
//            .also {
//                arrivingBusNum = it.size
//            }
//            .forEach { arrivingBus ->
//                if (arrivingBus != null && arrivingBus.estimatedArrival.isNotBlank()) {
//                    busArrivalEntityList.add(
//                        BusArrivalEntity(
//                            busServiceNumber = serviceNumber,
//                            busStopCode = busStopCode,
//                            seqNumber = ++seq,
//                            originCode = arrivingBus.originCode,
//                            destinationCode = arrivingBus.destinationCode,
//                            estimatedArrivalTimestamp =
//                            OffsetDateTime.parse(arrivingBus.estimatedArrival),
//                            latitude = arrivingBus.latitude.toDouble(),
//                            longitude = arrivingBus.longitude.toDouble(),
//                            visitNumber = arrivingBus.visitNumber.toInt(),
//                            load = BusLoad.valueOf(arrivingBus.load),
//                            feature = arrivingBus.feature,
//                            type = BusType.valueOf(arrivingBus.type),
//                        )
//                    )
//                }
//            }
//
//        // <= arrivingBusNum - 1 because seq is incremented while creating the instance of entity
//        // add error entities for remaining sequences
//        while (seq <= arrivingBusNum - 1) {
//
//            busArrivalEntityList.add(
//                getErrorBusArrivalEntity(
//                    busServiceNumber = serviceNumber,
//                    busStopCode = busStopCode,
//                    seq = ++seq
//                )
//            )
//        }
//
//        return busArrivalEntityList
//    }
//
//    private suspend inline fun getErrorBusArrivalEntity(
//        busStopCode: String,
//        busServiceNumber: String,
//        seq: Int
//    ): BusArrivalEntity {
//
//        val isOperating = busServiceNumber isBusOperatingAt busStopCode
//
//        return if (isOperating == false) {
//
//            BusArrivalEntity.notOperating(
//                busServiceNumber = busServiceNumber,
//                busStopCode = busStopCode,
//                seqNumber = seq
//            )
//
//        } else {
//
//            BusArrivalEntity.noData(
//                busServiceNumber = busServiceNumber,
//                busStopCode = busStopCode,
//                seqNumber = seq
//            )
//        }
//    }
//
//    /**
//     * @return true if operating
//     * @return false if not operating
//     * @return null if not enough data to determine
//     */
//    private suspend inline infix fun String.isBusOperatingAt(busStopCode: String): Boolean? {
//
//        val (
//            _,
//            _,
//            wdFirstBus: OffsetTime?,
//            wdLastBus: OffsetTime?,
//            satFirstBus: OffsetTime?,
//            satLastBus: OffsetTime?,
//            sunFirstBus: OffsetTime?,
//            sunLastBus: OffsetTime?
//        ) = operatingBusDao
//            .findByBusStopCodeAndBusServiceNumber(
//                busStopCode = busStopCode,
//                busServiceNumber = this
//            )
//            .firstOrNull()
//            ?: throw Exception(
//                "No operating bus row found for service number " +
//                        "$this and stop code " +
//                        "$busStopCode in local DB."
//            )
//
//        return when {
//            TimeUtil.isWeekday() -> {
//                OffsetTime.now().isInBetween(wdFirstBus, wdLastBus)
//            }
//            TimeUtil.isSaturday() -> {
//                OffsetTime.now().isInBetween(satFirstBus, satLastBus)
//            }
//            TimeUtil.isSunday() -> {
//                OffsetTime.now().isInBetween(sunFirstBus, sunLastBus)
//            }
//            else -> {
//                throw Exception("This day is neither a weekday nor a saturday or sunday.")
//            }
//        }
//    }
//
//    private fun OffsetTime.isInBetween(
//        first: OffsetTime?,
//        second: OffsetTime?
//    ): Boolean? {
//        if (first == null || second == null) return null
//        return isAfter(first) && isBefore(second)
//    }
//
//    private suspend inline fun updateBusOperator(
//        busStopCode: String,
//        busServiceNumber: String,
//        operator: String,
//    ) {
//        val busOperatorEntity =
//            BusOperatorEntity(
//                busServiceNumber = busServiceNumber,
//                busStopCode = busStopCode,
//                operator = operator
//            )
//
//        if (busOperatorDao
//                .findByBusServiceNumberAndBusStopCode(busServiceNumber, busStopCode)
//                .isEmpty()
//        ) {
//
//            busOperatorDao.insertAll(listOf(busOperatorEntity))
//
//        } else {
//            busOperatorDao.updateAll(listOf(busOperatorEntity))
//        }
//    }
//
//    @Synchronized
//    override fun onDestroy() {
//        super.onDestroy()
//        busArrivalNotificationManager.cancel(currentBusStopCode)
//        currentBusStopCode = ""
//        currentCoroutineJob?.cancel()
//        coroutineScope.cancel()
//        stopForeground(true)
//    }
//
//    companion object {
//        const val KEY_BUS_STOP_CODE = "bus_stop_code"
//        const val KEY_SHOW_NOTIFICATION = "show_notification"
//
//        private const val START_TYPE = Service.START_NOT_STICKY
//        private const val DELAY_TIME_MILLIS = 10000L
//        private const val TAG = "BusArrivalService"
//    }
//
//    class Helper @Inject constructor(private val activity: AppCompatActivity) {
//        fun start(busStopCode: String) {
//            activity.startService(
//                Intent(activity, BusArrivalService::class.java).apply {
//                    putExtra(KEY_BUS_STOP_CODE, busStopCode)
//                }
//            )
//        }
//
//        fun stop() {
//            activity.stopService(Intent(activity, BusArrivalService::class.java))
//        }
//    }
//}