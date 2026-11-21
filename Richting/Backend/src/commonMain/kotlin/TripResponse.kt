package richting

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

data class BoardLeg(
    val from: String, val to: String, val service: String, val number: String?,
    val departureSeconds: Long, val arrivalSeconds: Long, val platform: String?, val isWalk: Boolean
)

data class BoardJourney(
    val id: String, val legs: List<BoardLeg>, val transfers: Int,
    val images: List<String> = emptyList(), val facilities: List<String> = emptyList()
)

@Serializable
internal data class TripResponse(val trips: List<Trip>) {
    fun upcoming(from: String, to: String, now: Instant): List<BoardJourney> =
        trips.mapNotNull { it.journey(from, to) }
            .filter { it.legs.first().departureSeconds >= now.epochSeconds }
            .sortedWith(compareBy({ it.legs.first().departureSeconds }, { it.legs.last().arrivalSeconds }))
            .distinctBy { it.legs }
            .take(2)
}

@Serializable
internal data class Trip(val uid: String, val status: String, val transfers: Int, val legs: List<TripLeg>) {
    fun journey(from: String, to: String): BoardJourney? {
        if (status in setOf("CANCELLED", "CHANGE_NOT_POSSIBLE") || legs.isEmpty()) return null
        if (legs.first().origin.stationCode != from || legs.last().destination.stationCode != to) return null
        val itinerary = legs.map { it.toBoardLeg() ?: return null }
        if (itinerary.first().isWalk || itinerary.last().isWalk) return null
        if (itinerary.zipWithNext().any { (previous, next) -> previous.arrivalSeconds > next.departureSeconds }) return null
        return BoardJourney(uid, itinerary, transfers)
    }
}

@Serializable
internal data class TripLeg(
    val travelType: String, val origin: TripStop, val destination: TripStop,
    val product: TripProduct? = null, val cancelled: Boolean = false,
    val partCancelled: Boolean = false, val reachable: Boolean = true,
    val changePossible: Boolean = true
) {
    fun toBoardLeg(): BoardLeg? {
        if (cancelled || partCancelled || !reachable || !changePossible) return null
        val walk = travelType == "WALK"
        if (!walk && product?.type != "TRAIN") return null
        val departure = origin.time() ?: return null
        val arrival = destination.time() ?: return null
        if (arrival < departure) return null
        return BoardLeg(origin.name, destination.name, if (walk) "Walk" else product?.longCategoryName ?: "Train",
            product?.number, departure, arrival, origin.actualTrack ?: origin.plannedTrack, walk)
    }
}

@Serializable
internal data class TripProduct(val type: String? = null, val number: String? = null, val longCategoryName: String? = null)

@Serializable
internal data class TripStop(
    val name: String, val stationCode: String? = null,
    val plannedDateTime: String? = null, val actualDateTime: String? = null,
    val plannedTrack: String? = null, val actualTrack: String? = null
) {
    fun time(): Long? {
        val value = actualDateTime ?: plannedDateTime ?: return null
        // NS sends basic offsets such as +0100. Instant expects +01:00.
        val iso = value.replace(basicOffset, "$1:$2")
        return try { Instant.parse(iso).epochSeconds } catch (e: IllegalArgumentException) { null }
    }

    companion object {
        private val basicOffset = Regex("([+-][0-9]{2})([0-9]{2})$")
    }
}
