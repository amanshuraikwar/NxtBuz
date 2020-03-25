package io.github.amanshuraikwar.howmuch.domain.busstop

import android.location.Location
import android.util.Log
import io.github.amanshuraikwar.howmuch.data.model.BusStop
import io.github.amanshuraikwar.howmuch.data.user.UserRepository
import io.github.amanshuraikwar.howmuch.util.PermissionUtil
import javax.inject.Inject

private const val TAG = "GetBusStopsUseCase"

class GetBusStopsUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val permissionUtil: PermissionUtil
) {
    suspend operator fun invoke(limit: Int): BusStopsOutput {

        if (!permissionUtil.hasLocationPermission()) {
            return BusStopsOutput.PermissionsNotGranted
        }

        val location =
            userRepository.getLastKnownLocation() ?: return BusStopsOutput.CouldNotGetLocation

        Log.i(TAG, "invoke: Location lat=${location.latitude} lon=${location.longitude}")

        return BusStopsOutput.Success(
            userRepository.getCloseBusStops(location.latitude, location.longitude, limit),
            location.latitude to location.longitude
        )
    }

    suspend operator fun invoke(lat: Double, lon: Double, limit: Int): BusStopsOutput {

        Log.i(TAG, "invoke: Location lat=${lat} lon=${lon}")

        return BusStopsOutput.Success(
            userRepository.getCloseBusStops(lat, lon, limit),
            lat to lon
        )
    }
}

sealed class BusStopsOutput {
    object PermissionsNotGranted : BusStopsOutput()
    object CouldNotGetLocation : BusStopsOutput()
    data class Success(val busStopList: List<BusStop>, val latLon: Pair<Double, Double>) : BusStopsOutput()
}
