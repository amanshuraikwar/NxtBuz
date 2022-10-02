package io.github.amanshuraikwar.nxtbuz.remotedatasource

data class StationItemDto(
    val stationType: String,
    val code: String,
    val hasFacilities: Boolean,
    val hasDepartureTimes: Boolean,
    val hasTravelAssistance: Boolean,
    val names: StationNameDto,
    val land: String,
    val lat: Double,
    val lng: Double,
    val entryDate: String,
    val nearbyMeLocationId: NearbyMeLocationIdDto
)

data class StationNameDto(
    val long: String,
    val medium: String,
    val short: String
)

data class NearbyMeLocationIdDto(
    val value: String,
    val type: String
)