package io.github.amanshuraikwar.nxtbuz.domain.location

import android.app.Activity
import io.github.amanshuraikwar.nxtbuz.common.model.location.LocationOutput
import io.github.amanshuraikwar.nxtbuz.locationdata.LocationRepository
import java.lang.ref.WeakReference
import javax.inject.Inject

class GetLastKnownLocationUseCase @Inject constructor(
    _activity: Activity,
    private val locationRepository: LocationRepository,
) {
    val activity = WeakReference(_activity)
    suspend operator fun invoke(): LocationOutput {
        return locationRepository.getLastKnownLocation(
            activity.get()
                ?: return LocationOutput.Error(
                    reason = "activity is null"
                )
        )
    }
}


