package io.github.amanshuraikwar.howmuch.ui.onboarding.setup

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import io.github.amanshuraikwar.howmuch.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.domain.transaction.SetupNewSpreadSheetUseCase
import io.github.amanshuraikwar.howmuch.domain.user.GetUserStateUseCase
import io.github.amanshuraikwar.howmuch.domain.user.UserState
import io.github.amanshuraikwar.howmuch.util.asEvent
import io.github.amanshuraikwar.howmuch.util.safeLaunch
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SetupViewModel"

class SetupViewModel @Inject constructor(
    private val getUserStateUseCase: GetUserStateUseCase,
    private val setupNewSpreadSheetUseCase: SetupNewSpreadSheetUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _error = MutableLiveData<Exception>()
    val error =
        _error
            .map {
                Log.e(TAG, "onError: ", it)
                it.message ?: "Something went wrong."
            }
            .asEvent()

    private val _userState = MutableLiveData<UserState>()
    val userState = _userState.map { it }

    init {
        initiateSetup()
    }

    private fun initiateSetup() = viewModelScope.launch(dispatcherProvider.io) {
        safeLaunch(_error) {
            var userState = getUserStateUseCase.invoke()
            _userState.postValue(userState)
            if (userState is UserState.SignedIn) {
                setupNewSpreadSheetUseCase.invoke()
                userState = getUserStateUseCase.invoke()
                _userState.postValue(userState)
            }
        }
    }
}