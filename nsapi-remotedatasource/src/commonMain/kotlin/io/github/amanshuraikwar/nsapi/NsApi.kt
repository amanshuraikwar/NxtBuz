package io.github.amanshuraikwar.nsapi

import io.github.amanshuraikwar.nsapi.model.StationsResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.observer.ResponseObserver
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
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
        return client.get("$baseUrl/stations") {
            addSubscriptionKey()
        }
    }

    companion object {
        private const val ENDPOINT = "https://gateway.apiportal.ns.nl/reisinformatie-api/api/v2"

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