package io.github.amanshuraikwar.dynamo.api

import io.github.amanshuraikwar.dynamo.api.model.ThemeApiResponse
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.observer.*
import io.ktor.client.request.*
import io.ktor.utils.io.*
import kotlinx.serialization.json.Json

class ThemeApi private constructor(
    private val client: HttpClient,
    private val themeApiUrl: String,
) {
    suspend fun getTheme() =
        client.get<ThemeApiResponse>(themeApiUrl)

    companion object {
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