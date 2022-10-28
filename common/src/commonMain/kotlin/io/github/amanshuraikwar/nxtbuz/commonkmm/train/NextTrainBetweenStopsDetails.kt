package io.github.amanshuraikwar.nxtbuz.commonkmm.train

data class NextTrainBetweenStopsDetails(
    val trainCode: String,
    val trainCategoryName: String,
    val fromTrainStopName: String,
    val toTrainStopName: String,
    val facilities: List<TrainFacility>,
    val rollingStockImages: List<String>,
    val length: Int,
    val lengthInMeters: Int,
    val departureFromIntendedSource: String,
    val arrivalAtIntendedDestination: String,
    val updatedAt: String
)