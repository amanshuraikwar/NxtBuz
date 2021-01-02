package io.github.amanshuraikwar.nxtbuz.listitem

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.common.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.common.model.BusArrival
import io.github.amanshuraikwar.nxtbuz.common.model.BusLoad
import io.github.amanshuraikwar.nxtbuz.common.model.BusType
import kotlinx.android.synthetic.main.item_bus_arrival_compact.view.*

@ListItem(layoutResName = "item_bus_arrival_compact")
class BusArrivalCompactItem(
    val busStopCode: String,
    val busArrival: BusArrival,
    private val onStarToggle: (busStopCode: String, busArrival: BusArrival) -> Unit,
    private val onBusServiceClicked: (serviceNumber: String) -> Unit
) : RecyclerViewListItem {

    private var nextDeparture1Tv: String = "N/A"
    private var crowdedIv1: Int = R.drawable.ic_round_cloud_off_24
    private var wheelChairAccessIv1: Int = R.drawable.ic_round_cloud_off_24
    private var busTypeIv1: Int = R.drawable.ic_round_cloud_off_24

    private var nextDeparture2Tv: String = "N/A"
    private var crowdedIv2: Int = R.drawable.ic_round_cloud_off_24
    private var wheelChairAccessIv2: Int = R.drawable.ic_round_cloud_off_24
    private var busTypeIv2: Int = R.drawable.ic_round_cloud_off_24

    private var nextDeparture3Tv: String = "N/A"
    private var crowdedIv3: Int = R.drawable.ic_round_cloud_off_24
    private var wheelChairAccessIv3: Int = R.drawable.ic_round_cloud_off_24
    private var busTypeIv3: Int = R.drawable.ic_round_cloud_off_24

    init {
        when (busArrival.arrivals) {
            is Arrivals.Arriving -> {
                (busArrival.arrivals as Arrivals.Arriving).nextArrivingBus.let {
                    nextDeparture1Tv = it.arrival
                    crowdedIv1 =
                        when (it.load) {
                            BusLoad.SEA -> {
                                R.drawable.ic_load_1_24
                            }
                            BusLoad.SDA -> {
                                R.drawable.ic_load_2_24
                            }
                            BusLoad.LSD -> {
                                R.drawable.ic_load_3_24
                            }
                        }
                    wheelChairAccessIv1 =
                        if (it.feature == "WAB") {
                            R.drawable.ic_round_accessible_24
                        } else {
                            R.drawable.ic_round_accessible_inactive_24
                        }
                    busTypeIv1 =
                        when (it.type) {
                            BusType.SD -> R.drawable.ic_round_directions_bus_24
                            BusType.DD -> R.drawable.ic_bus_dd_24
                            BusType.BD -> R.drawable.ic_bus_feeder_24
                        }
                }
                if ((busArrival.arrivals as Arrivals.Arriving).followingArrivingBusList.isNotEmpty()) {
                    (busArrival.arrivals as Arrivals.Arriving).followingArrivingBusList[0].let {
                        nextDeparture2Tv = it.arrival
                        crowdedIv2 =
                            when (it.load) {
                                BusLoad.SEA -> {
                                    R.drawable.ic_load_1_24
                                }
                                BusLoad.SDA -> {
                                    R.drawable.ic_load_2_24
                                }
                                BusLoad.LSD -> {
                                    R.drawable.ic_load_3_24
                                }
                            }
                        wheelChairAccessIv2 =
                            if (it.feature == "WAB") {
                                R.drawable.ic_round_accessible_24
                            } else {
                                R.drawable.ic_round_accessible_inactive_24
                            }
                        busTypeIv2 =
                            when (it.type) {
                                BusType.SD -> R.drawable.ic_round_directions_bus_24
                                BusType.DD -> R.drawable.ic_bus_dd_24
                                BusType.BD -> R.drawable.ic_bus_feeder_24
                            }
                    }
                }
                if ((busArrival.arrivals as Arrivals.Arriving).followingArrivingBusList.size >= 2) {
                    (busArrival.arrivals as Arrivals.Arriving).followingArrivingBusList[1].let {
                        nextDeparture3Tv = it.arrival
                        crowdedIv3 =
                            when (it.load) {
                                BusLoad.SEA -> {
                                    R.drawable.ic_load_1_24
                                }
                                BusLoad.SDA -> {
                                    R.drawable.ic_load_2_24
                                }
                                BusLoad.LSD -> {
                                    R.drawable.ic_load_3_24
                                }
                            }
                        wheelChairAccessIv3 =
                            if (it.feature == "WAB") {
                                R.drawable.ic_round_accessible_24
                            } else {
                                R.drawable.ic_round_accessible_inactive_24
                            }
                        busTypeIv3 =
                            when (it.type) {
                                BusType.SD -> R.drawable.ic_round_directions_bus_24
                                BusType.DD -> R.drawable.ic_bus_dd_24
                                BusType.BD -> R.drawable.ic_bus_feeder_24
                            }
                    }
                }
            }
            is Arrivals.NotOperating,
            is Arrivals.DataNotAvailable -> {
                throw IllegalArgumentException("Bus arrivals cannot be error.")
            }
        }
    }

    private fun AppCompatImageView.toggleStar() {
        if (busArrival.starred) {
            busArrival.starred = false
            setImageResource(R.drawable.ic_round_star_border_24)
        } else {
            busArrival.starred = true
            setImageResource(R.drawable.ic_round_star_24)
        }
    }

    override fun bind(view: View, activity: FragmentActivity) {

        val busInfo = busArrival.destinationStopDescription

        view.parentCv.setOnClickListener { onBusServiceClicked(busArrival.serviceNumber) }

        view.serviceNumberTv.text = busArrival.serviceNumber
        view.busInfoTv.text = busInfo

        if (busArrival.starred) {
            view.starIv.setImageResource(R.drawable.ic_round_star_24)
        } else {
            view.starIv.setImageResource(R.drawable.ic_round_star_border_24)
        }

        view.starIv.setOnClickListener {
            view.starIv.toggleStar()
            onStarToggle(busStopCode, busArrival)
        }

        view.nextDepartureTv.text = if (nextDeparture1Tv == "Arr") {
            "arriving now"
        } else {
            "in $nextDeparture1Tv mins"
        }
    }
}