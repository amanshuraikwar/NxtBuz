package io.github.amanshuraikwar.howmuch.ui.list

import android.view.View
import androidx.annotation.ColorInt
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.model.Arrivals
import io.github.amanshuraikwar.howmuch.data.model.BusArrival
import io.github.amanshuraikwar.howmuch.data.model.BusLoad
import io.github.amanshuraikwar.howmuch.data.model.BusType
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.item_bus_arrival_compact.view.*

@ListItem(layoutResId = R.layout.item_bus_arrival_compact)
class BusArrivalCompactItem(
    private val busArrival: BusArrival
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {
        view.serviceNumberTv.text = busArrival.serviceNumber
        view.busInfoTv.text =
            "${busArrival.operator}  •  ${busArrival.stopSequence}  •  ${busArrival.distance}KM  •  ${busArrival.direction}\n${busArrival.originStopDescription}"
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
                    view.nextDeparture2Tv.text = "N/A"
                    view.crowdedIv2.setImageResource(R.drawable.ic_round_cloud_off_24)
                    view.wheelChairAccessIv2.setImageResource(R.drawable.ic_round_cloud_off_24)
                    view.busTypeIv2.setImageResource(R.drawable.ic_round_cloud_off_24)
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
                    view.nextDeparture3Tv.text = "N/A"
                    view.crowdedIv3.setImageResource(R.drawable.ic_round_cloud_off_24)
                    view.wheelChairAccessIv3.setImageResource(R.drawable.ic_round_cloud_off_24)
                    view.busTypeIv3.setImageResource(R.drawable.ic_round_cloud_off_24)
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