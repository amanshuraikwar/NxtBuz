package io.github.amanshuraikwar.nxtbuz.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.amanshuraikwar.nxtbuz.domain.location.CleanupLocationUpdatesUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val cleanupLocationUpdatesUseCase: CleanupLocationUpdatesUseCase,
) : ViewModel() {

    override fun onCleared() {
        viewModelScope.launch {
            cleanupLocationUpdatesUseCase()
        }
    }
}