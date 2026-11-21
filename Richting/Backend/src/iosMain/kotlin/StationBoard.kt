package richting

import io.github.amanshuraikwar.nsapi.provider.NsApiFactory
import io.github.amanshuraikwar.nxtbuz.commonkmm.DispatcherProviderFactory
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainFacility
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.datetime.Clock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class StationBoard(private val key: String, directory: String, suite: String) {
    private val client = HttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    private val repository = NsApiFactory(
        suite, DispatcherProviderFactory.getDispatcherProvider(), key, false, directory
    ).create()

    @Throws(Exception::class)
    suspend fun journeys(from: String, to: String): List<BoardJourney> = withContext(Dispatchers.Main) {
        if (from == to) return@withContext emptyList()
        val response = withTimeout(15_000L) {
            client.get<String>("https://gateway.apiportal.ns.nl/reisinformatie-api/api/v3/trips") {
                header("Ocp-Apim-Subscription-Key", key)
                parameter("fromStation", from)
                parameter("toStation", to)
                parameter("searchForArrival", false)
                parameter("lang", "en")
                parameter("excludeBus", true)
                parameter("excludeTram", true)
                parameter("excludeMetro", true)
            }
        }
        val trips = json.decodeFromString<TripResponse>(response).upcoming(from, to, Clock.System.now())
        trips.map { async { withFirstTrainDetails(it) } }.awaitAll()
    }

    // Rolling stock is optional. Regional trains can have a valid itinerary but no stock data.
    private suspend fun withFirstTrainDetails(trip: BoardJourney): BoardJourney {
        val number = trip.legs.first().number ?: return trip
        val details = try {
            withTimeoutOrNull(3_000L) { repository.getTrainDetails(number) }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        } ?: return trip
        return trip.copy(
            images = details.rollingStock.mapNotNull { it.imageUrl },
            facilities = details.facilities.mapNotNull {
                when (it) {
                    TrainFacility.WIFI -> "wifi"
                    TrainFacility.BICYCLE -> "bicycle"
                    TrainFacility.POWER_SOCKETS -> "powerplug"
                    else -> null
                }
            }.distinct()
        )
    }
}
