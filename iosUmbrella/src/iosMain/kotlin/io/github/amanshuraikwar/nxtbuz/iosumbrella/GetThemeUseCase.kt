package io.github.amanshuraikwar.nxtbuz.iosumbrella

import io.github.amanshuraikwar.dynamo.DynamoTheme
import io.github.amanshuraikwar.dynamo.DynamoThemeRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import kotlin.native.concurrent.freeze

class GetThemeUseCase(
    private val dynamoThemeRepository: DynamoThemeRepository
) {
    fun getThemeSync(): DynamoTheme {
        return dynamoThemeRepository.getThemeData()
    }

    fun getThemeUpdates(callback: (DynamoTheme) -> Unit) {
        IosDataCoroutineScopeProvider.coroutineScope.launch(
            CoroutineExceptionHandler { _, th ->
                th.printStackTrace()
                println(th)
            }
        ) {
            dynamoThemeRepository
                .getThemeDataFlow()
                .collect {
                    callback(it.freeze())
                }
        }
    }
}