package io.github.amanshuraikwar.howmuch.ui.onboarding

import androidx.lifecycle.*
import io.github.amanshuraikwar.howmuch.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.util.asEvent
import io.github.amanshuraikwar.howmuch.R
import javax.inject.Inject

class OnboardingViewModel @Inject constructor(
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _loading = MutableLiveData<Loading>()
    val loading = _loading.asEvent()

    private val _error = MutableLiveData<Int>()
    val error = _error.asEvent()

    private val _initiateSignIn = MutableLiveData<Any>()
    val initiateSignIn = _initiateSignIn.asEvent()

    init {

    }

    fun onSignInClicked() {
        _loading.value = Loading.SignIn
        _initiateSignIn.value = Any()
    }

    fun onSignInResult(successful: Boolean) {
        _loading.value = Loading.NoLoading(Loading.SignIn)
        if (successful) {

        } else {
            _error.postValue(R.string.error_sign_in_failed)
        }
    }
}

sealed class Loading {
    object SignIn : Loading()
    object Setup : Loading()
    data class NoLoading(val from: Loading): Loading()
}