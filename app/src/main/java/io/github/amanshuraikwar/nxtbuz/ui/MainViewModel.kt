package io.github.amanshuraikwar.nxtbuz.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.amanshuraikwar.nxtbuz.domain.location.CleanupLocationUpdatesUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class MainViewModel @Inject constructor(
    @Named("onBackPressed") private val _onBackPressed: MutableLiveData<Unit>,
    private val cleanupLocationUpdatesUseCase: CleanupLocationUpdatesUseCase,
) : ViewModel() {
    fun onBackPressed() {
        _onBackPressed.postValue(Unit)
    }

    override fun onCleared() {
        // TODO: 2/1/21 will this execute if scope is cancelled after cleared?
        viewModelScope.launch {
            cleanupLocationUpdatesUseCase()
        }
    }
}