package io.github.amanshuraikwar.nxtbuz.common.model

import org.threeten.bp.OffsetDateTime

data class StarredBusArrivalNotification(
    val notificationId: Int,
    val arrivalTimeStamp: OffsetDateTime,
    val arrivingInMin: Int
)