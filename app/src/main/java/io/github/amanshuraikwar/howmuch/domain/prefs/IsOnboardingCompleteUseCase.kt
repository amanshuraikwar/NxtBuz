package io.github.amanshuraikwar.howmuch.domain.prefs

import io.github.amanshuraikwar.howmuch.data.prefs.SourcesRepository
import javax.inject.Inject

/**
 * Returns whether onboarding has been completed.
 */
class IsOnboardingCompleteUseCase @Inject constructor(
    private val sourcesRepository: SourcesRepository
) {
    suspend operator fun invoke(): Boolean {
        return sourcesRepository.isOnboardingComplete()
    }
}
