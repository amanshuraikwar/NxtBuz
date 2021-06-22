package io.github.amanshuraikwar.nxtbuz.launcher

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.LaunchDestination
import io.github.amanshuraikwar.nxtbuz.common.model.user.UserState
import io.github.amanshuraikwar.nxtbuz.common.util.asEvent
import io.github.amanshuraikwar.nxtbuz.domain.user.GetUserStateUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Logic for determining which screen to send users to on app launch.
 */
class LauncherViewModel @Inject constructor(
    private val getUserStateUseCase: GetUserStateUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {
    private val _launchDestination = MutableLiveData<LaunchDestination>()
    val launchDestination = _launchDestination.asEvent()

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
    }

    init {
        FirebaseCrashlytics.getInstance().setCustomKey("viewModel", TAG)
        checkOnboarding()
    }

    private fun checkOnboarding() {
        viewModelScope.launch(dispatcherProvider.main + errorHandler) {
            _launchDestination.value =
                if (getUserStateUseCase() is UserState.SetupComplete)
                    LaunchDestination.MAIN_ACTIVITY
                else
                    LaunchDestination.ONBOARDING

        }
    }

    companion object {
        private const val TAG = "LaunchViewModel"
    }
}
