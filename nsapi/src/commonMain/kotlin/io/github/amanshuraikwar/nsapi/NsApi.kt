package io.github.amanshuraikwar.nsapi

import io.github.amanshuraikwar.nsapi.model.ArrivalsResponseDto
import io.github.amanshuraikwar.nsapi.model.DeparturesResponseDto
import io.github.amanshuraikwar.nsapi.model.StationsResponseDto
import io.github.amanshuraikwar.nsapi.model.TrainCrowdForecastStationDto
import io.github.amanshuraikwar.nsapi.model.TrainInfoDto
import io.github.amanshuraikwar.nsapi.model.TrainJourneyDetailsResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.observer.ResponseObserver
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.utils.io.readUTF8Line
import kotlinx.serialization.json.Json

internal class NsApi(
    private val client: HttpClient,
    private val baseUrl: String,
    private val subscriptionKey: String,
) {
    private fun HttpRequestBuilder.addSubscriptionKey(): HttpRequestBuilder {
        return apply {
            header("Ocp-Apim-Subscription-Key", subscriptionKey)
        }
    }

    suspend fun getStations(): StationsResponseDto {
        return client.get("$baseUrl/reisinformatie-api/api/v2/stations") {
            addSubscriptionKey()
        }
    }

    suspend fun getTrainDepartures(stationCode: String): DeparturesResponseDto {
        return client.get("$baseUrl/reisinformatie-api/api/v2/departures") {
            addSubscriptionKey()
            url {
                parameter("station", stationCode)
            }
        }
    }

    suspend fun getTrainArrivals(stationCode: String): ArrivalsResponseDto {
        return client.get("$baseUrl/reisinformatie-api/api/v2/arrivals") {
            addSubscriptionKey()
            url {
                parameter("station", stationCode)
            }
        }
    }

    /**
     * Get train crowd info for a train code
     *
     * Also known as:
     * https://apiportal.ns.nl/docs/services/virtual-train-api/operations/getTreinInfo_1/console
     */
    suspend fun getTrainCrowdForecast(trainCode: String): List<TrainCrowdForecastStationDto> {
        return client.get("$baseUrl/virtual-train-api/api/v1/prognose/$trainCode") {
            addSubscriptionKey()
        }
    }

    /**
     * Get train information about multiple trains
     * Includes rolling stock images and information
     *
     * Also known as:
     * https://apiportal.ns.nl/docs/services/virtual-train-api/operations/getTreinInformatie_2/console
     */
    suspend fun getTrainInformation(
        trainCodes: List<String>,
        stationCodes: List<String>
    ): List<TrainInfoDto> {
        return client.get("$baseUrl/virtual-train-api/api/v1/trein") {
            addSubscriptionKey()
            url {
                parameter("ids", trainCodes.fold("") { r, t -> "$r,$t" }.drop(1))
                parameter("stations", stationCodes.fold("") { r, t -> "$r,$t" }.drop(1))
                // options:
                // platformitems -> platform info
                // zitplaats -> seat
                // cta -> cta ????
                // drukte -> rush
                // druktev2 -> pressurev2
                parameter("features", "zitplaats")
                // don't get all stations
                parameter("all", "false")
            }
        }
    }

    /**
     * Get train route / journey details
     *
     * Also known as:
     * https://apiportal.ns.nl/docs/services/reisinformatie-api/operations/getJourneyDetail/console
     */
    suspend fun getTrainJourneyDetails(trainCode: String): TrainJourneyDetailsResponseDto {
        return client.get("$baseUrl/reisinformatie-api/api/v2/journey") {
            addSubscriptionKey()
            url {
                parameter("train", trainCode)
            }
        }
    }

    companion object {
        private const val ENDPOINT = "https://gateway.apiportal.ns.nl"

        private fun createJson() = Json { isLenient = true; ignoreUnknownKeys = true }

        private fun createHttpClient(
            json: Json = createJson(),
            enableNetworkLogs: Boolean
        ) = HttpClient {
            install(JsonFeature) {
                serializer = KotlinxSerializer(json)
            }

            if (enableNetworkLogs) {
                install(ResponseObserver) {
                    onResponse { response ->
                        println("Response: $response")
                        println("Response: ${response.content.readUTF8Line()}")
                    }
                }
            }
        }

        private fun createHttpClient(
            engine: HttpClientEngine,
            json: Json = createJson(),
            enableNetworkLogs: Boolean
        ) = HttpClient(engine) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(json)
            }

            if (enableNetworkLogs) {
                install(ResponseObserver) {
                    onResponse { response ->
                        println("Response: $response")
                        println("Response: ${response.content.readUTF8Line()}")
                    }
                }
            }
        }

        fun createInstance(
            addLoggingInterceptors: Boolean,
            subscriptionKey: String,
        ): NsApi {
            return NsApi(
                client = createHttpClient(enableNetworkLogs = addLoggingInterceptors),
                baseUrl = ENDPOINT,
                subscriptionKey = subscriptionKey
            )
        }

        fun createInstance(
            engine: HttpClientEngine,
            addLoggingInterceptors: Boolean,
            subscriptionKey: String,
        ): NsApi {
            return NsApi(
                client = createHttpClient(
                    engine = engine,
                    enableNetworkLogs = addLoggingInterceptors
                ),
                baseUrl = ENDPOINT,
                subscriptionKey = subscriptionKey
            )
        }
    }
}