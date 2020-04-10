package io.github.amanshuraikwar.nxtbuz.ui.list

import android.view.View
import androidx.annotation.ColorInt
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.data.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.data.model.BusArrival
import io.github.amanshuraikwar.nxtbuz.data.model.BusLoad
import io.github.amanshuraikwar.nxtbuz.data.model.BusType
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.item_bus_arrival.view.*

@ListItem(layoutResId = R.layout.item_bus_arrival)
class BusArrivalItem(
    private val busArrival: BusArrival,
    @ColorInt private val colorControlNormal: Int,
    @ColorInt private val colorControlActivated: Int
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {
        view.serviceNumberTv.text = busArrival.serviceNumber
        view.busInfoTv.text =
            "${busArrival.operator}\n${busArrival.stopSequence} • ${busArrival.distance}KM"
        view.busRouteOriginTv.text = "${busArrival.originStopDescription}"
        view.busRouteDestinationTv.text = "${busArrival.destinationStopDescription}"
        when (busArrival.arrivals) {
            is Arrivals.Arriving -> {
                busArrival.arrivals.arrivingBusList[0].let {
                    view.nextDeparture1Tv.text = it.arrival
                    view.crowdedIv1.setImageResource(
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
                    )
                    view.wheelChairAccessIv1.setImageResource(
                        if (it.feature == "WAB") {
                            R.drawable.ic_round_accessible_24
                        } else {
                            R.drawable.ic_round_accessible_inactive_24
                        }
                    )
                    view.busTypeIv1.setImageResource(
                        when (it.type) {
                            BusType.SD -> R.drawable.ic_round_directions_bus_24
                            BusType.DD -> R.drawable.ic_bus_dd_24
                            BusType.BD -> R.drawable.ic_bus_feeder_24
                        }
                    )
                }
                if (busArrival.arrivals.arrivingBusList.size >= 2) {
                    busArrival.arrivals.arrivingBusList[1].let {
                        view.nextDeparture2Tv.text = it.arrival
                        view.crowdedIv2.setImageResource(
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
                        )
                        view.wheelChairAccessIv2.setImageResource(
                            if (it.feature == "WAB") {
                                R.drawable.ic_round_accessible_24
                            } else {
                                R.drawable.ic_round_accessible_inactive_24
                            }
                        )
                        view.busTypeIv2.setImageResource(
                            when (it.type) {
                                BusType.SD -> R.drawable.ic_round_directions_bus_24
                                BusType.DD -> R.drawable.ic_bus_dd_24
                                BusType.BD -> R.drawable.ic_bus_feeder_24
                            }
                        )
                    }
                } else {
                    // todo
                }
                if (busArrival.arrivals.arrivingBusList.size >= 3) {
                    busArrival.arrivals.arrivingBusList[2].let {
                        view.nextDeparture3Tv.text = it.arrival
                        view.crowdedIv3.setImageResource(
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
                        )
                        view.wheelChairAccessIv3.setImageResource(
                            if (it.feature == "WAB") {
                                R.drawable.ic_round_accessible_24
                            } else {
                                R.drawable.ic_round_accessible_inactive_24
                            }
                        )
                        view.busTypeIv3.setImageResource(
                            when (it.type) {
                                BusType.SD -> R.drawable.ic_round_directions_bus_24
                                BusType.DD -> R.drawable.ic_bus_dd_24
                                BusType.BD -> R.drawable.ic_bus_feeder_24
                            }
                        )
                    }
                } else {
                    // todo
                }
            }
            is Arrivals.NotOperating -> {
                // todo
                view.nextDeparture1Tv.text = "Not Opr"
                view.crowdedIv1.setImageResource(R.drawable.ic_load_0_24)
            }
        }
    }
}