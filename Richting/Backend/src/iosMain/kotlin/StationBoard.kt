package richting

import io.github.amanshuraikwar.nsapi.provider.NsApiFactory
import io.github.amanshuraikwar.nxtbuz.commonkmm.DispatcherProviderFactory
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class BoardTrain(
    val number: String, val service: String, val departureSeconds: Long,
    val arrivalSeconds: Long?, val platform: String?, val images: List<String>,
    val hasWifi: Boolean, val hasBicycleSpace: Boolean, val hasPower: Boolean
)

/** Swift-facing adapter over the existing NS repository. Only trains stopping downstream qualify. */
class StationBoard(key: String, directory: String, suite: String) {
    private val dispatchers = DispatcherProviderFactory.getDispatcherProvider()
    private val repository = NsApiFactory(
        suite, dispatchers, key, false, directory
    ).create()

    @Throws(Exception::class)
    suspend fun departures(from: String, to: String): List<BoardTrain> {
        if (from == to) return emptyList()
        return withContext(dispatchers.main) {
            withTimeout(20_000L) { matchingDepartures(from, to) }
        }
    }

    private suspend fun matchingDepartures(from: String, to: String): List<BoardTrain> {
        val now = Clock.System.now()
        val departures = repository.getTrainDepartures(from)
            .filter { it.departureStatus != TrainDepartureStatus.CANCELLED && it.departureTime() >= now }
            .sortedBy { it.departureTime() }
            .distinctBy { it.trainCode }
        val result = mutableListOf<BoardTrain>()
        var failure: Exception? = null
        for (departure in departures) {
            val details = try {
                repository.getTrainDetails(departure.trainCode)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                failure = error
                continue
            } ?: continue
            val originIndex = details.route.indexOfFirst { node ->
                node.trainStopCode == from && (node.type is TrainRouteNodeType.Origin || node.type is TrainRouteNodeType.Stop)
            }
            if (originIndex < 0) continue
            val origin = details.route[originIndex].type
            val departureTiming = (when (origin) {
                is TrainRouteNodeType.Origin -> origin.departureTiming
                is TrainRouteNodeType.Stop -> origin.departureTiming
                else -> null
            }) as? TrainRouteNodeTiming.Available
            if (departureTiming?.cancelled == true) continue
            val destination = details.route.drop(originIndex + 1).firstOrNull { node ->
                node.trainStopCode == to && (node.type is TrainRouteNodeType.Stop || node.type is TrainRouteNodeType.Destination)
            } ?: continue
            val arrival = (when (val type = destination.type) {
                is TrainRouteNodeType.Stop -> type.arrivalTiming
                is TrainRouteNodeType.Destination -> type.arrivalTiming
                else -> null
            }) as? TrainRouteNodeTiming.Available
            if (arrival?.cancelled == true) continue
            result += BoardTrain(
                departure.trainCode, departure.trainCategoryName, departure.departureTime().epochSeconds,
                (arrival?.actualTimeInstant ?: arrival?.plannedTimeInstant)?.epochSeconds,
                departureTiming?.actualTrack ?: departure.track,
                details.rollingStock.mapNotNull { it.imageUrl },
                TrainFacility.WIFI in details.facilities, TrainFacility.BICYCLE in details.facilities,
                TrainFacility.POWER_SOCKETS in details.facilities
            )
            if (result.size == 2) break
        }
        if (result.isEmpty() && failure != null) throw failure
        return result
    }

    private fun TrainDeparture.departureTime(): Instant = actualDepartureInstant ?: plannedDepartureInstant
}
