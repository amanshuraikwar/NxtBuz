package richting

import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class TripResponseTest {
    private fun stop(code: String, time: String) = TripStop(
        name = code, stationCode = code, plannedDateTime = "2026-05-01T${time}:00+0200"
    )

    private val train = TripProduct("TRAIN", "123", "Intercity")
    private val first = TripLeg("PUBLIC_TRANSIT", stop("A", "12:10"), stop("B", "12:25"), train)
    private val second = TripLeg("PUBLIC_TRANSIT", stop("B", "12:30"), stop("C", "12:50"), train.copy(number = "456"))
    private val connecting = Trip("connection", "NORMAL", 1, listOf(first, second))
    private val direct = Trip("direct", "NORMAL", 0, listOf(first.copy(
        origin = stop("A", "12:20"), destination = stop("C", "12:55")
    )))
    private val now = Instant.parse("2026-05-01T10:00:00Z")

    @Test
    fun nextTwoTripsUseActualDepartureAndFinalArrival() {
        val delayed = first.copy(origin = first.origin.copy(
            actualDateTime = "2026-05-01T12:12:00+0200", plannedTrack = "1", actualTrack = "3"
        ))
        val trip = connecting.copy(legs = listOf(delayed, second))
        val result = TripResponse(listOf(direct, trip, trip.copy(uid = "duplicate"))).upcoming("A", "C", now)
        assertEquals(listOf("connection", "direct"), result.map { it.id })
        assertEquals(Instant.parse("2026-05-01T10:12:00Z").epochSeconds, result.first().legs.first().departureSeconds)
        assertEquals("3", result.first().legs.first().platform)
        assertEquals(Instant.parse("2026-05-01T10:50:00Z").epochSeconds, result.first().legs.last().arrivalSeconds)
        assertEquals(1, result.first().transfers)
    }

    @Test
    fun alternativesSharingAFirstTrainAreDistinctAndResultsAreLimitedToTwo() {
        val laterConnection = connecting.copy(uid = "later", legs = listOf(first, second.copy(
            destination = stop("C", "12:59"), product = train.copy(number = "789")
        )))
        val result = TripResponse(listOf(direct, laterConnection, connecting)).upcoming("A", "C", now)
        assertEquals(listOf("connection", "later"), result.map { it.id })
    }

    @Test
    fun walkBetweenTrainsIsKeptAndUnusableConnectionsAreSkipped() {
        val walk = TripLeg("WALK", stop("B", "12:25"), stop("B2", "12:28"))
        val valid = connecting.copy(legs = listOf(first, walk, second.copy(origin = stop("B2", "12:32"))))
        val unavailable = listOf(
            connecting.copy(status = "CANCELLED"),
            connecting.copy(legs = listOf(first, second.copy(cancelled = true))),
            connecting.copy(legs = listOf(first, second.copy(origin = stop("B", "12:24")))),
            connecting.copy(legs = listOf(first, second.copy(product = TripProduct("BUS")))),
            direct.copy(legs = listOf(first.copy(origin = stop("A", "11:59"), destination = stop("C", "12:20"))))
        )
        val result = TripResponse(unavailable + valid).upcoming("A", "C", now)
        assertEquals(listOf(false, true, false), result.single().legs.map { it.isWalk })
    }
}
