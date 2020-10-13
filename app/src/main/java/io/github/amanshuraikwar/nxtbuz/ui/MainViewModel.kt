package io.github.amanshuraikwar.nxtbuz.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject
import javax.inject.Named

class MainViewModel @Inject constructor(
    @Named("onBackPressed") private val _onBackPressed: MutableLiveData<Unit>
) : ViewModel() {
    fun onBackPressed() {
        _onBackPressed.postValue(Unit)
    }
}