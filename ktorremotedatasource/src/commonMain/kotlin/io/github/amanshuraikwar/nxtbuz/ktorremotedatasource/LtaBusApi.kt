package io.github.amanshuraikwar.nxtbuz.ktorremotedatasource

import io.github.amanshuraikwar.nxtbuz.ktorremotedatasource.model.BusArrivalsResponseDto
import io.github.amanshuraikwar.nxtbuz.ktorremotedatasource.model.BusRoutesResponseDto
import io.github.amanshuraikwar.nxtbuz.ktorremotedatasource.model.BusStopsResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.observer.ResponseObserver
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.UrlEncodingOption
import io.ktor.utils.io.readUTF8Line
import kotlinx.serialization.json.Json

internal class LtaBusApi internal constructor(
    private val client: HttpClient,
    private val baseUrl: String,
    private val ltaAccountKey: String,
) {
    private fun HttpRequestBuilder.addLtaAccountKey(): HttpRequestBuilder {
        return apply {
            header("AccountKey", ltaAccountKey)
        }
    }

    suspend fun getBusStops(skip: Int = 0): BusStopsResponseDto {
        return client.get("$baseUrl/BusStops") {
            url {
                parameter("\$skip", skip)
                // to prevent encoding '$'
                // weird query param names required by the api -_-
                parameters.urlEncodingOption = UrlEncodingOption.NO_ENCODING
            }

            addLtaAccountKey()
        }
    }

    suspend fun getBusArrivals(
        busStopCode: String,
        busServiceNumber: String? = null
    ): BusArrivalsResponseDto {
        return client.get("$baseUrl/BusArrivalv2") {
            url {
                parameter("BusStopCode", busStopCode)
                parameter("ServiceNo", busServiceNumber)
            }

            addLtaAccountKey()
        }
    }

    suspend fun getBusRoutes(skip: Int = 0): BusRoutesResponseDto {
        return client.get("$baseUrl/BusRoutes") {
            url {
                parameter("\$skip", skip)
                // to prevent encoding '$'
                // weird query param names required by the api -_-
                parameters.urlEncodingOption = UrlEncodingOption.NO_ENCODING
            }

            addLtaAccountKey()
        }
    }

    companion object {
        private const val ENDPOINT = "http://datamall2.mytransport.sg/ltaodataservice"

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
            ltaAccountKey: String,
        ): LtaBusApi {
            return LtaBusApi(
                client = createHttpClient(enableNetworkLogs = addLoggingInterceptors),
                baseUrl = ENDPOINT,
                ltaAccountKey = ltaAccountKey
            )
        }

        fun createInstance(
            engine: HttpClientEngine,
            addLoggingInterceptors: Boolean,
            ltaAccountKey: String,
        ): LtaBusApi {
            return LtaBusApi(
                client = createHttpClient(
                    engine = engine,
                    enableNetworkLogs = addLoggingInterceptors
                ),
                baseUrl = ENDPOINT,
                ltaAccountKey = ltaAccountKey
            )
        }
    }
}