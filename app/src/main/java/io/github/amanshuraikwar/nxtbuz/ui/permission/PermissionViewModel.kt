package io.github.amanshuraikwar.nxtbuz.ui.permission

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import io.github.amanshuraikwar.nxtbuz.data.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.util.permission.PermissionUtil
import io.github.amanshuraikwar.nxtbuz.util.asEvent
import io.github.amanshuraikwar.nxtbuz.util.permission.PermissionStatus
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class PermissionViewModel @Inject constructor(
    private val permissionUtil: PermissionUtil,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _error = MutableLiveData<Throwable>()
    val error =
        _error
            .map {
                "Something went wrong. Please try again."
            }
            .asEvent()

    private val _nextPage = MutableLiveData<Unit>()
    val nextPage = _nextPage.asEvent()

    private val _showSkipBtn = MutableLiveData<Unit>()
    val showSkipBtn = _showSkipBtn.map { it }

    private val _showGoToSettingsBtn = MutableLiveData<Unit>()
    val showGoToSettingsBtn = _showGoToSettingsBtn.map { it }

    private val _showContinueBtn = MutableLiveData<Unit>()
    val showContinueBtn = _showContinueBtn.map { it }

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        _error.postValue(th)
    }

    fun checkPermissions() =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            when (permissionUtil.hasLocationPermission()) {
                PermissionStatus.GRANTED -> {
                    _showContinueBtn.postValue(Unit)
                }
                PermissionStatus.DENIED -> {
                    _showSkipBtn.postValue(Unit)
                }
                PermissionStatus.DENIED_PERMANENTLY -> {
                    _showSkipBtn.postValue(Unit)
                    _showGoToSettingsBtn.postValue(Unit)
                }
            }
        }

    fun askPermissions() = viewModelScope.launch(dispatcherProvider.io + errorHandler) {
        val permissionResult = permissionUtil.askPermission()
        Log.i(TAG, "askPermissions: Permission result = ${permissionResult.name}")
        when (permissionResult) {
            PermissionStatus.GRANTED -> {
                _nextPage.postValue(Unit)
            }
            PermissionStatus.DENIED -> {
                _showSkipBtn.postValue(Unit)
            }
            PermissionStatus.DENIED_PERMANENTLY -> {
                _showSkipBtn.postValue(Unit)
                _showGoToSettingsBtn.postValue(Unit)
            }
        }
    }

    companion object {
        private const val TAG = "PermissionViewModel"
    }
}