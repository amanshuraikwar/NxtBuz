package io.github.amanshuraikwar.nxtbuz.onboarding.permission

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.amanshuraikwar.nxtbuz.common.CoroutinesDispatcherProvider
import io.github.amanshuraikwar.nxtbuz.common.model.location.PermissionStatus
import io.github.amanshuraikwar.nxtbuz.common.model.SettingsState
import io.github.amanshuraikwar.nxtbuz.common.util.asEvent
import io.github.amanshuraikwar.nxtbuz.common.util.asLiveData
import io.github.amanshuraikwar.nxtbuz.common.util.location.LocationUtil
import io.github.amanshuraikwar.nxtbuz.common.util.permission.PermissionUtil
import io.github.amanshuraikwar.nxtbuz.onboarding.R
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

class PermissionViewModel @Inject constructor(
    private val permissionUtil: PermissionUtil,
    private val locationUtil: LocationUtil,
    dispatcherProvider: CoroutinesDispatcherProvider
) : ViewModel() {

    private val _nextPage = MutableLiveData<Unit>()
    val nextPage = _nextPage.asEvent()

    private val _showSkipBtn = MutableLiveData<Unit>()
    val showSkipBtn = _showSkipBtn.asLiveData()

    private val _showGoToSettingsBtn = MutableLiveData<Unit>()
    val showGoToSettingsBtn = _showGoToSettingsBtn.asLiveData()

    private val _showContinueBtn = MutableLiveData<Unit>()
    val showContinueBtn = _showContinueBtn.asLiveData()

    private val _showEnableSettingsBtn = MutableLiveData<Unit>()
    val showEnableSettingsBtn = _showEnableSettingsBtn.asLiveData()

    private val _error = MutableLiveData<Throwable>()
    val error = _error.map { R.string.error_title_default }.asEvent()

    private val errorHandler = CoroutineExceptionHandler { _, th ->
        Log.e(TAG, "errorHandler: $th", th)
        FirebaseCrashlytics.getInstance().recordException(th)
        _error.postValue(th)
    }
    private val coroutineContext = errorHandler + dispatcherProvider.computation

    init {
        FirebaseCrashlytics.getInstance().setCustomKey("viewModel", TAG)
    }

    fun checkPermissions() {
        viewModelScope.launch(coroutineContext) {
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
    }

    fun askPermissions() = viewModelScope.launch(coroutineContext) {
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

    fun enableSettings() = viewModelScope.launch(coroutineContext) {
        when (locationUtil.enableSettings()) {
            is SettingsState.Enabled -> {
                _nextPage.postValue(Unit)
            }
            SettingsState.Resolvable -> {
                FirebaseCrashlytics.getInstance().log(
                    "enableSettings: Resulted in SettingsState.Resolvable")
                Log.wtf(TAG, "enableSettings: Resulted in SettingsState.Resolvable")
            }
            SettingsState.UserCancelled -> {
                _showSkipBtn.postValue(Unit)
            }
            is SettingsState.UnResolvable -> {
                FirebaseCrashlytics.getInstance().log(
                    "enableSettings: Resulted in SettingsState.UnResolvable")
                Log.e(TAG, "enableSettings: Resulted in SettingsState.UnResolvable")
            }
        }
    }

    companion object {
        private const val TAG = "PermissionViewModel"
    }
}