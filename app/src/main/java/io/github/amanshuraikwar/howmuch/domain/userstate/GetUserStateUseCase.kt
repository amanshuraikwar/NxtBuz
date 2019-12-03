package io.github.amanshuraikwar.howmuch.domain.userstate

import io.github.amanshuraikwar.howmuch.data.prefs.SourcesRepository
import javax.inject.Inject

class GetUserStateUseCase @Inject constructor(
    private val sourcesRepository: SourcesRepository
) {
    suspend operator fun invoke(): UserState {
        val user = sourcesRepository.getUser() ?: return UserState.NotSignedIn
        val spreadSheetId =
            sourcesRepository.getSpreadSheetId(user) ?: return UserState.SignedIn(user)
        return UserState.SpreadSheetCreated(user, spreadSheetId)
    }
}
