package io.github.amanshuraikwar.howmuch.domain.prefs

import android.util.Log
import io.github.amanshuraikwar.howmuch.data.prefs.SourcesRepository
import javax.inject.Inject

/**
 * Returns whether onboarding has been completed.
 */
class OnboardingCompletedUseCase @Inject constructor(
    private val sourcesRepository: SourcesRepository
) {
    suspend operator fun invoke(): Boolean {
        Log.d("Thread", "checkOnboarding: ${Thread.currentThread()}")
        return sourcesRepository.isOnboardingComplete()
    }
}
