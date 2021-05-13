package io.github.amanshuraikwar.nxtbuz.onboarding.setup

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.SetupState
import io.github.amanshuraikwar.nxtbuz.common.model.UserState
import io.github.amanshuraikwar.nxtbuz.common.util.asEvent
import io.github.amanshuraikwar.nxtbuz.domain.user.DoSetupUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.GetUserStateUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class SetupViewModel @Inject constructor(
    private val getUserStateUseCase: GetUserStateUseCase,
    private val doSetupUseCase: DoSetupUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _error = MutableLiveData<Throwable>()
    val error =
        _error
            .map {
                "Something went wrong. Please try again."
            }
            .asEvent()

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
        _error.postValue(th)
    }

    private val _userState = MutableLiveData<UserState>()
    val userState = _userState.map { it }

    private val _setupProgress = MutableLiveData<Double>()
    val setupProgress = _setupProgress.asEvent()

    init {
        FirebaseCrashlytics.getInstance().setCustomKey("viewModel", TAG)
        initiateSetup()
    }

    fun initiateSetup() = viewModelScope.launch(dispatcherProvider.io + errorHandler) {
        var userState = getUserStateUseCase()
        _userState.postValue(userState)
        if (userState is UserState.New) {
            doSetupUseCase().collect { value ->
                when (value) {
                    is SetupState.InProgress -> {
                        _setupProgress.postValue(value.progress)
                    }
                    is SetupState.Complete -> {
                        // do nothing
                    }
                }
            }
            userState = getUserStateUseCase()
            _userState.postValue(userState)
        }
    }

    companion object {
        private const val TAG = "SetupViewModel"
    }
}