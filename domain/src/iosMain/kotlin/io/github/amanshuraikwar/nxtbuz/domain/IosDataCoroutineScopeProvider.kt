package io.github.amanshuraikwar.nxtbuz.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

object IosDataCoroutineScopeProvider {
    val coroutineScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}