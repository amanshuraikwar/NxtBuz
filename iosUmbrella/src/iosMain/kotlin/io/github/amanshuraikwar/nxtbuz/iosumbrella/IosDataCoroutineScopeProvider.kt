package io.github.amanshuraikwar.nxtbuz.iosumbrella

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

object IosDataCoroutineScopeProvider {
    val coroutineScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob())
    }
}