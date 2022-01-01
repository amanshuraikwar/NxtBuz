package io.github.amanshuraikwar.testutil

import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

suspend fun Job.joinWithExpiry(
    expiryMillis: Int,
    intervalMillis: Int = 300
): Boolean {
    var time = 0
    while (time < expiryMillis) {
        if (isCompleted) {
            join()
            return true
        }
        time += intervalMillis
        delay(intervalMillis.toLong())
    }
    cancel()
    return false
}

val FakeCoroutinesDispatcherProvider = CoroutinesDispatcherProvider(
    main = Dispatchers.Default,
    computation = Dispatchers.Default,
    io = Dispatchers.Default,
    pool8 = Dispatchers.Default,
    map = Dispatchers.Default,
    arrivalService = Dispatchers.Default,
    location = Dispatchers.Default,
)