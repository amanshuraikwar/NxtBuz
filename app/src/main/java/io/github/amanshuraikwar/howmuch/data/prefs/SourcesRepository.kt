package io.github.amanshuraikwar.howmuch.data.prefs

import android.util.Log
import io.github.amanshuraikwar.howmuch.data.CoroutinesDispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Manage saving and retrieving data sources from disk.
 */
@Singleton
class SourcesRepository @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    suspend fun isOnboardingComplete(): Boolean = withContext(dispatcherProvider.io) {
        Log.d("Thread", "checkOnboarding: ${Thread.currentThread()}")
        return@withContext preferenceStorage.onboardingCompleted
    }
}
