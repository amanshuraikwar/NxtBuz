package io.github.amanshuraikwar.nxtbuz.onboarding.permission

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.PermissionStatus
import io.github.amanshuraikwar.nxtbuz.common.model.SettingsState
import io.github.amanshuraikwar.nxtbuz.common.util.asEvent
import io.github.amanshuraikwar.nxtbuz.common.util.location.LocationUtil
import io.github.amanshuraikwar.nxtbuz.common.util.permission.PermissionUtil
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class PermissionViewModel @Inject constructor(
    private val permissionUtil: PermissionUtil,
    private val locationUtil: LocationUtil,
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

    private val _showEnableSettingsBtn = MutableLiveData<Unit>()
    val showEnableSettingsBtn = _showEnableSettingsBtn.map { it }

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
        _error.postValue(th)
    }

    init {
        FirebaseCrashlytics.getInstance().setCustomKey("viewModel", TAG)
    }

    fun checkPermissions() =
        viewModelScope.launch(dispatcherProvider.io + errorHandler) {
            when (permissionUtil.hasLocationPermission()) {
                PermissionStatus.GRANTED -> {
                    if (locationUtil.settingEnabled() == SettingsState.Enabled) {
                        _showContinueBtn.postValue(Unit)
                    } else {
                        _showEnableSettingsBtn.postValue(Unit)
                    }
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
                if (locationUtil.settingEnabled() == SettingsState.Enabled) {
                    _nextPage.postValue(Unit)
                } else {
                    _showEnableSettingsBtn.postValue(Unit)
                }
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

    fun enableSettings() = viewModelScope.launch(dispatcherProvider.io + errorHandler) {
        when (val settingsState = locationUtil.enableSettings()) {
            is SettingsState.Enabled -> {
                _nextPage.postValue(Unit)
            }
            SettingsState.Resolvable -> {
                Log.wtf(TAG, "enableSettings: Resulted in SettingsState.Resolvable")
            }
            SettingsState.UserCancelled -> {
                _showSkipBtn.postValue(Unit)
            }
            is SettingsState.UnResolvable -> {
                Log.e(TAG, "enableSettings: Resulted in SettingsState.UnResolvable")
            }
        }

    }

    companion object {
        private const val TAG = "PermissionViewModel"
    }
}