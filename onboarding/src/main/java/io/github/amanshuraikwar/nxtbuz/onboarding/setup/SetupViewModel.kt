package io.github.amanshuraikwar.nxtbuz.onboarding.setup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.commonkmm.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.onboarding.setup.worker.SetupWorker.Companion.getSetupProgress
import io.github.amanshuraikwar.nxtbuz.onboarding.setup.worker.SetupWorkerUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class SetupViewModel @Inject constructor(
    private val setupWorkerUseCase: SetupWorkerUseCase,
    @Named("appVersionInfo") private val appVersionInfo: String,
    dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {
    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
    }
    private val coroutineContext = errorHandler + dispatcherProvider.computation

    private val _screenState = MutableStateFlow(
        SetupScreenState(appVersionInfo, SetupProgressState.Starting)
    )

    val screenState = _screenState.asStateFlow()

    init {
        FirebaseCrashlytics.getInstance().setCustomKey("viewModel", TAG)
    }

    fun initiateSetup() {
        viewModelScope.launch(coroutineContext) {
            setupWorkerUseCase()
                ?.collect { workInfo ->
                    when (workInfo.state) {
                        WorkInfo.State.ENQUEUED -> {
                            _screenState.value = SetupScreenState(
                                appVersionInfo,
                                SetupProgressState.Starting
                            )
                        }
                        WorkInfo.State.RUNNING -> {
                            _screenState.value = SetupScreenState(
                                appVersionInfo,
                                SetupProgressState.InProgress(
                                    progress = workInfo.getSetupProgress()
                                )
                            )
                        }
                        WorkInfo.State.SUCCEEDED -> {
                            _screenState.value = SetupScreenState(
                                appVersionInfo,
                                SetupProgressState.SetupComplete
                            )
                        }
                        WorkInfo.State.FAILED,
                        WorkInfo.State.BLOCKED -> {
                            _screenState.value = SetupScreenState(
                                appVersionInfo,
                                SetupProgressState.Error("Setup failed, please try again :(")
                            )
                        }
                        WorkInfo.State.CANCELLED -> {
                            _screenState.value = SetupScreenState(
                                appVersionInfo,
                                SetupProgressState.Error("Setup was cancelled, please try again :(")
                            )
                        }
                    }
                }
        }
    }

    companion object {
        private const val TAG = "SetupViewModel"
    }
}