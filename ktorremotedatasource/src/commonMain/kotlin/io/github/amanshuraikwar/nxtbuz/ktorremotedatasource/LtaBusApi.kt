package io.github.amanshuraikwar.nxtbuz.ktorremotedatasource

import io.github.amanshuraikwar.nxtbuz.ktorremotedatasource.model.BusArrivalsResponseDto
import io.github.amanshuraikwar.nxtbuz.ktorremotedatasource.model.BusRoutesResponseDto
import io.github.amanshuraikwar.nxtbuz.ktorremotedatasource.model.BusStopsResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
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
                //parameter("\$skip", skip)

                // to prevent encoding '$'
                // weird query param names required by the api -_-
                encodedParameters.append("\$skip", "$skip")
            }

            addLtaAccountKey()
        }.body()
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
        }.body()
    }

    suspend fun getBusRoutes(skip: Int = 0): BusRoutesResponseDto {
        return client.get("$baseUrl/BusRoutes") {
            url {
                //parameter("\$skip", skip)

                // to prevent encoding '$'
                // weird query param names required by the api -_-
                encodedParameters.append("\$skip", "$skip")
            }

            addLtaAccountKey()
        }.body()
    }

    companion object {
        private const val ENDPOINT = "http://datamall2.mytransport.sg/ltaodataservice"

        private fun createJson() = Json {
            isLenient = true
            ignoreUnknownKeys = true
        }

        private fun createHttpClient(
            json: Json = createJson(),
            enableNetworkLogs: Boolean
        ) = HttpClient {
            config(json = json, enableNetworkLogs = enableNetworkLogs)
        }

        private fun createHttpClient(
            engine: HttpClientEngine,
            json: Json = createJson(),
            enableNetworkLogs: Boolean
        ) = HttpClient(engine) {
            config(json = json, enableNetworkLogs = enableNetworkLogs)
        }

        private fun HttpClientConfig<*>.config(
            json: Json,
            enableNetworkLogs: Boolean,
        ) {
            if (enableNetworkLogs) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.ALL
                }

                install(ResponseObserver) {
                    onResponse { response ->
                        println("Response: $response")
                        println("Response: ${response.bodyAsText()}")
                    }
                }
            }

            install(ContentNegotiation) {
                json(json)
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