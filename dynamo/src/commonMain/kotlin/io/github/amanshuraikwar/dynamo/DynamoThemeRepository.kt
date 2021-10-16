package io.github.amanshuraikwar.dynamo

import com.russhwolf.settings.Settings
import io.github.amanshuraikwar.dynamo.api.ThemeApi
import io.github.amanshuraikwar.dynamo.api.model.ThemeApiResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DynamoThemeRepository internal constructor(
    defaultTheme: DynamoTheme,
    private val apiDataConverter: (ThemeApiResponse) -> DynamoTheme,
    private val themeApi: ThemeApi,
) {
    private val settings = Settings()

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        th.printStackTrace()
        println(th)
    }
    private val repositoryScope = MainScope() + Dispatchers.Default + errorHandler

    private val themeData = MutableStateFlow(
        settings
            .getString(
                PREFS_THEME,
                ""
            )
            .takeIf {
                it.isNotEmpty()
            }
            ?.let {
                Json.decodeFromString<ThemeApiResponse>(
                    it
                )
            }
            ?.let(apiDataConverter)
            ?: defaultTheme
    )

    fun getThemeData() = themeData.value

    fun getThemeDataFlow(): StateFlow<DynamoTheme> {
        repositoryScope.launch {
            themeApi.getTheme().let { response ->
                themeData.value = apiDataConverter(response)
                settings.putString(PREFS_THEME, Json.encodeToString(response))
            }
        }
        return themeData
    }

    companion object {
        private const val PREFS_THEME = "dynamo_theme_data"
    }
}