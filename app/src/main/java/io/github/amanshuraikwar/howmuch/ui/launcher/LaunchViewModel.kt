package io.github.amanshuraikwar.howmuch.ui.launcher

import androidx.lifecycle.*
import io.github.amanshuraikwar.howmuch.data.di.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.domain.user.GetUserStateUseCase
import io.github.amanshuraikwar.howmuch.data.user.UserState
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
                if (getUserStateUseCase() is UserState.SetupComplete)
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
