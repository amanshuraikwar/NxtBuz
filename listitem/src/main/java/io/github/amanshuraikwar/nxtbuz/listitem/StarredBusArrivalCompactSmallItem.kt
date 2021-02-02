package io.github.amanshuraikwar.nxtbuz.listitem

import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.common.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.common.model.BusLoad
import io.github.amanshuraikwar.nxtbuz.common.model.BusType
import io.github.amanshuraikwar.nxtbuz.common.model.StarredBusArrival
import kotlinx.android.synthetic.main.item_starred_bus_arrival_compact_small.view.*

@ListItem(layoutResName = "item_starred_bus_arrival_compact_small")
class StarredBusArrivalCompactSmallItem(
    val busArrival: StarredBusArrival,
    private val onClicked: (busStopCode: String, busServiceNumber: String) -> Unit,
    private val onLongClick: (busStopCode: String, busServiceNumber: String) -> Unit
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
                                R.drawable.ic_bus_load_1_16
                            }
                            BusLoad.SDA -> {
                                R.drawable.ic_bus_load_2_16
                            }
                            BusLoad.LSD -> {
                                R.drawable.ic_bus_load_3_16
                            }
                        }
                    wheelChairAccessIv1 =
                        if (it.feature == "WAB") {
                            R.drawable.ic_accessible_16
                        } else {
                            R.drawable.ic_not_accessible_16
                        }
                    busTypeIv1 =
                        when (it.type) {
                            BusType.SD -> R.drawable.ic_bus_normal_16
                            BusType.DD -> R.drawable.ic_bus_dd_16
                            BusType.BD -> R.drawable.ic_bus_feeder_16
                        }
                }
                if ((busArrival.arrivals as Arrivals.Arriving).followingArrivingBusList.isNotEmpty()) {
                    (busArrival.arrivals as Arrivals.Arriving).followingArrivingBusList[0].let {
                        nextDeparture2Tv = it.arrival
                        crowdedIv2 =
                            when (it.load) {
                                BusLoad.SEA -> {
                                    R.drawable.ic_bus_load_1_16
                                }
                                BusLoad.SDA -> {
                                    R.drawable.ic_bus_load_2_16
                                }
                                BusLoad.LSD -> {
                                    R.drawable.ic_bus_load_3_16
                                }
                            }
                        wheelChairAccessIv2 =
                            if (it.feature == "WAB") {
                                R.drawable.ic_accessible_16
                            } else {
                                R.drawable.ic_not_accessible_16
                            }
                        busTypeIv2 =
                            when (it.type) {
                                BusType.SD -> R.drawable.ic_bus_normal_16
                                BusType.DD -> R.drawable.ic_bus_dd_16
                                BusType.BD -> R.drawable.ic_bus_feeder_16
                            }
                    }
                }
                if ((busArrival.arrivals as Arrivals.Arriving).followingArrivingBusList.size >= 2) {
                    (busArrival.arrivals as Arrivals.Arriving).followingArrivingBusList[1].let {
                        nextDeparture3Tv = it.arrival
                        crowdedIv3 =
                            when (it.load) {
                                BusLoad.SEA -> {
                                    R.drawable.ic_bus_load_1_16
                                }
                                BusLoad.SDA -> {
                                    R.drawable.ic_bus_load_2_16
                                }
                                BusLoad.LSD -> {
                                    R.drawable.ic_bus_load_3_16
                                }
                            }
                        wheelChairAccessIv3 =
                            if (it.feature == "WAB") {
                                R.drawable.ic_accessible_16
                            } else {
                                R.drawable.ic_not_accessible_16
                            }
                        busTypeIv3 =
                            when (it.type) {
                                BusType.SD -> R.drawable.ic_bus_normal_16
                                BusType.DD -> R.drawable.ic_bus_dd_16
                                BusType.BD -> R.drawable.ic_bus_feeder_16
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

    override fun bind(view: View, activity: FragmentActivity) {

        view.parentCv.setOnClickListener {
            onClicked(busArrival.busStopCode, busArrival.busServiceNumber)
        }

        view.parentCv.setOnLongClickListener {
            onLongClick(busArrival.busStopCode, busArrival.busServiceNumber)
            true
        }

        view.serviceNumberTv.text = busArrival.busServiceNumber

        view.nextDeparture1Tv.text = nextDeparture1Tv
        view.crowdedIv1.setImageResource(crowdedIv1)
        view.wheelChairAccessIv1.setImageResource(wheelChairAccessIv1)
        view.busTypeIv1.setImageResource(busTypeIv1)

        view.nextDeparture2Tv.text = nextDeparture2Tv
        view.crowdedIv2.setImageResource(crowdedIv2)
        view.wheelChairAccessIv2.setImageResource(wheelChairAccessIv2)
        view.busTypeIv2.setImageResource(busTypeIv2)

        view.nextDeparture3Tv.text = nextDeparture3Tv
        view.crowdedIv3.setImageResource(crowdedIv3)
        view.wheelChairAccessIv3.setImageResource(wheelChairAccessIv3)
        view.busTypeIv3.setImageResource(busTypeIv3)
    }
}