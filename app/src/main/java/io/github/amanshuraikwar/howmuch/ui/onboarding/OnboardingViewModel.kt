package io.github.amanshuraikwar.howmuch.ui.onboarding

import androidx.lifecycle.*
import io.github.amanshuraikwar.howmuch.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.util.asEvent
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.domain.model.User
import io.github.amanshuraikwar.howmuch.domain.userstate.GetUserStateUseCase
import io.github.amanshuraikwar.howmuch.domain.userstate.UserState
import kotlinx.coroutines.launch
import javax.inject.Inject

class OnboardingViewModel @Inject constructor(
    private val getUserStateUseCase: GetUserStateUseCase,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _error = MutableLiveData<Int>()
    val error = _error.asEvent()

    private val _initiateSignIn = MutableLiveData<Any>()
    val initiateSignIn = _initiateSignIn.asEvent()

    private val _userState = MutableLiveData<UserState>()
    val userState = _userState.map { it }

    init {
        checkUserState()
    }

    private fun checkUserState() {
        viewModelScope.launch(dispatcherProvider.io) {
            _userState.postValue(getUserStateUseCase.invoke())
        }
    }

    fun onSignInClicked() {
        _initiateSignIn.value = Any()
    }

    fun onSignInResult(successful: Boolean) {
        if (successful) {
            checkUserState()
        } else {
            _error.value = R.string.error_sign_in_failed
        }
    }
}