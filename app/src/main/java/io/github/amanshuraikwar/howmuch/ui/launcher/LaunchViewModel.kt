package io.github.amanshuraikwar.howmuch.ui.launcher

import android.util.Log
import androidx.lifecycle.*
import io.github.amanshuraikwar.howmuch.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.domain.prefs.OnboardingCompletedUseCase
import io.github.amanshuraikwar.howmuch.util.asEvent
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Logic for determining which screen to send users to on app launch.
 */
class LaunchViewModel @Inject constructor(
    private val onboardingCompletedUseCase: OnboardingCompletedUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _launchDestination = MutableLiveData<LaunchDestination>()
    val launchDestination = _launchDestination.asEvent()

    init {
        checkOnboarding()
    }

    private fun checkOnboarding() {
        viewModelScope.launch(dispatcherProvider.main) {
            Log.d("Thread", "checkOnboarding: ${Thread.currentThread()}")
            _launchDestination.value =
                if (onboardingCompletedUseCase.invoke())
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
