package io.github.amanshuraikwar.nxtbuz.commonkmm.user

sealed class LaunchBusStopsPage {
    object NearBy : LaunchBusStopsPage()
    object Starred : LaunchBusStopsPage()

    override fun toString(): String {
        return when (this) {
            NearBy -> "NearBy"
            Starred -> "Starred"
        }
    }

    companion object {
        fun String.toLaunchBusStopPage(): LaunchBusStopsPage? {
            return valueOf(this)
        }

        fun valueOf(value: String): LaunchBusStopsPage? {
            return when (value) {
                "NearBy" -> NearBy
                "Starred" -> Starred
                else -> null
            }
        }
    }
}