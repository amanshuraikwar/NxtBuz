package io.github.amanshuraikwar.howmuch.ui.main.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.di.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.domain.user.GetUserStateUseCase
import io.github.amanshuraikwar.howmuch.domain.user.SignOutUseCase
import io.github.amanshuraikwar.howmuch.domain.user.UserState
import io.github.amanshuraikwar.howmuch.util.asEvent
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _error = MutableLiveData<Int>()
    val error = _error.asEvent()
}