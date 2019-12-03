package io.github.amanshuraikwar.howmuch.ui.launcher

import android.util.Log
import androidx.lifecycle.*
import io.github.amanshuraikwar.howmuch.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.domain.prefs.IsOnboardingCompleteUseCase
import io.github.amanshuraikwar.howmuch.domain.userstate.GetUserStateUseCase
import io.github.amanshuraikwar.howmuch.domain.userstate.UserState
import io.github.amanshuraikwar.howmuch.util.asEvent
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Logic for determining which screen to send users to on app launch.
 */
class LaunchViewModel @Inject constructor(
    private val getUserStateUseCase: GetUserStateUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _launchDestination = MutableLiveData<LaunchDestination>()
    val launchDestination = _launchDestination.asEvent()

    init {
        checkOnboarding()
    }

    private fun checkOnboarding() {
        viewModelScope.launch(dispatcherProvider.main) {
            _launchDestination.value =
                if (getUserStateUseCase.invoke() is UserState.SpreadSheetCreated)
                    LaunchDestination.MAIN_ACTIVITY
                else
                    LaunchDestination.ONBOARDING

        }
    }
}

enum class LaunchDestination {
    ONBOARDING,
    MAIN_ACTIVITY
}
