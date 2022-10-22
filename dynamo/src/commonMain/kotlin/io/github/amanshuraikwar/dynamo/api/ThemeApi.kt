package io.github.amanshuraikwar.dynamo.api

import io.github.amanshuraikwar.dynamo.api.model.ThemeApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ThemeApi private constructor(
    private val client: HttpClient,
    private val themeApiUrl: String,
) {
    suspend fun getTheme() =
        client.get(themeApiUrl).body<ThemeApiResponse>()

    companion object {
        private fun createJson() = Json { isLenient = true; ignoreUnknownKeys = true }

        private fun createHttpClient(
            json: Json = createJson(),
            enableNetworkLogs: Boolean
        ) = HttpClient {
            config(json, enableNetworkLogs)
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

        internal fun createInstance(
            addLoggingInterceptors: Boolean,
            themeApiUrl: String,
        ): ThemeApi {
            return ThemeApi(
                client = createHttpClient(enableNetworkLogs = addLoggingInterceptors),
                themeApiUrl = themeApiUrl
            )
        }
    }
}