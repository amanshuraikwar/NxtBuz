package io.github.amanshuraikwar.nxtbuz.domain.location

import android.app.Activity
import io.github.amanshuraikwar.nxtbuz.common.model.location.LocationSettingsState
import io.github.amanshuraikwar.nxtbuz.locationdata.LocationRepository
import java.lang.ref.WeakReference
import javax.inject.Inject

class GetLocationSettingStateUseCase @Inject constructor(
    _activity: Activity,
    private val locationRepository: LocationRepository,
) {
    val activity = WeakReference(_activity)
    suspend operator fun invoke(): LocationSettingsState {
        return locationRepository.getLocationSettingsState(
            activity.get()
                ?: return LocationSettingsState.Error(
                    reason = "activity is null"
                )
        )
    }
}


