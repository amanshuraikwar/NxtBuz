package richting

import io.github.amanshuraikwar.nsapi.provider.NsApiFactory
import io.github.amanshuraikwar.nxtbuz.commonkmm.DispatcherProviderFactory
import io.github.amanshuraikwar.nxtbuz.commonkmm.train.TrainDetails
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
        val numbers = trips.flatMap { trip -> trip.legs.filterNot { it.isWalk }.mapNotNull { it.number } }.distinct()
        val stock = numbers.map { number -> async { number to trainDetails(number) } }.awaitAll().toMap()
        trips.map { trip ->
            trip.copy(
                legs = trip.legs.map { leg ->
                    leg.copy(images = stock[leg.number]?.rollingStock.orEmpty().mapNotNull { it.imageUrl })
                },
                facilities = stock[trip.legs.first().number]?.facilities.orEmpty().mapNotNull {
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

    // A train can have a valid itinerary even when its rolling stock is unavailable.
    private suspend fun trainDetails(number: String): TrainDetails? = try {
        withTimeoutOrNull(3_000L) { repository.getTrainDetails(number) }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }
}
