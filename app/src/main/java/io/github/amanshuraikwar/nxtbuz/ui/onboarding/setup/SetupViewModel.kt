package io.github.amanshuraikwar.nxtbuz.ui.onboarding.setup

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.data.SetupState
import io.github.amanshuraikwar.nxtbuz.data.user.UserRepository
import io.github.amanshuraikwar.nxtbuz.data.user.model.UserState
import io.github.amanshuraikwar.nxtbuz.domain.setup.SetupUseCase
import io.github.amanshuraikwar.nxtbuz.domain.user.GetUserStateUseCase
import io.github.amanshuraikwar.nxtbuz.util.asEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SetupViewModel"

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class SetupViewModel @Inject constructor(
    private val getUserStateUseCase: GetUserStateUseCase,
    private val setupUseCase: SetupUseCase,
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
        _error.postValue(th)
    }

    private val _userState = MutableLiveData<UserState>()
    val userState = _userState.map { it }

    private val _setupProgress = MutableLiveData<Double>()
    val setupProgress = _setupProgress.asEvent()

    init {
        initiateSetup()
    }

    @InternalCoroutinesApi
    @ExperimentalCoroutinesApi
    fun initiateSetup() = viewModelScope.launch(dispatcherProvider.io + errorHandler) {
        var userState = getUserStateUseCase()
        _userState.postValue(userState)
        if (userState is UserState.New) {
            setupUseCase().collect(
                object : FlowCollector<SetupState> {
                    override suspend fun emit(value: SetupState) {
                        when (value) {
                            is SetupState.InProgress -> {
                                _setupProgress.postValue(value.progress)
                            }
                            is SetupState.Complete -> {
                                // do nothing
                            }
                        }
                    }
                }
            )
            userState = getUserStateUseCase.invoke()
            _userState.postValue(userState)
        }
    }
}