package io.github.amanshuraikwar.howmuch.ui.onboarding.signin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.domain.user.GetUserStateUseCase
import io.github.amanshuraikwar.howmuch.domain.user.SignOutUseCase
import io.github.amanshuraikwar.howmuch.domain.user.UserState
import io.github.amanshuraikwar.howmuch.util.asEvent
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignInViewModel @Inject constructor(
    private val getUserStateUseCase: GetUserStateUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _error = MutableLiveData<Int>()
    val error = _error.asEvent()

    private val _userState = MutableLiveData<UserState>()
    val userState = _userState.map { it }

    init {
        checkUserState()
    }

    private fun checkUserState() = viewModelScope.launch(dispatcherProvider.io) {
        _userState.postValue(getUserStateUseCase.invoke())
    }

    fun onSignInResult(successful: Boolean) {
        if (successful) {
            checkUserState()
        } else {
            _error.value = R.string.error_sign_in_failed
        }
    }

    fun onSignedOut() = viewModelScope.launch(dispatcherProvider.io) {
        signOutUseCase()
        checkUserState()
    }
}