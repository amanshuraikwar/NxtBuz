package io.github.amanshuraikwar.howmuch.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.amanshuraikwar.howmuch.data.di.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.howmuch.util.asEvent
import javax.inject.Inject
import javax.inject.Named

class MainViewModel @Inject constructor(
    @Named("onBackPressed") private val _onBackPressed: MutableLiveData<Unit>
) : ViewModel() {
    fun onBackPressed() {
        _onBackPressed.postValue(Unit)
    }
}