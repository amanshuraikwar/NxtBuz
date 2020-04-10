package io.github.amanshuraikwar.nxtbuz.ui.main.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.amanshuraikwar.nxtbuz.data.di.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.util.asEvent
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _error = MutableLiveData<Int>()
    val error = _error.asEvent()
}