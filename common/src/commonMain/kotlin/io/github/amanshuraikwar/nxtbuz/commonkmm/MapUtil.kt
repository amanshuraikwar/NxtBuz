package io.github.amanshuraikwar.nxtbuz.commonkmm

import kotlin.math.*

object MapUtil {
    fun measureDistanceMetres(
        lat1: Double, lng1: Double, lat2: Double, lng2: Double
    ): Double {
        val r = 6378.137 // Radius of earth in KM
        val dLat = lat2 * PI / 180 - lat1 * PI / 180
        val dLon = lng2 * PI / 180 - lng1 * PI / 180
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1 * PI / 180) * cos(lat2 * PI / 180) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val d = r * c
        return d * 1000 // meters
    }
}