package io.github.amanshuraikwar.nxtbuz.iosumbrella

import io.github.amanshuraikwar.nxtbuz.userdata.UserRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import kotlin.native.concurrent.freeze

class GetUseSystemThemeUseCase(
    private val userRepository: UserRepository
) {
    fun getSync(): Boolean {
        return userRepository.getUseSystemThemeSync()
    }

    fun getUpdates(callback: (Boolean) -> Unit) {
        IosDataCoroutineScopeProvider.coroutineScope.launch(
            CoroutineExceptionHandler { _, th ->
                th.printStackTrace()
                println(th)
            }
        ) {
            userRepository
                .getUseSystemThemeUpdates()
                .collect {
                    callback(it.freeze())
                }
        }
    }

    fun set(newValue: Boolean) {
        IosDataCoroutineScopeProvider.coroutineScope.launch(
            CoroutineExceptionHandler { _, th ->
                th.printStackTrace()
                println(th)
            }
        ) {
            userRepository.setUseSystemTheme(newValue)
        }
    }
}