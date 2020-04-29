package io.github.amanshuraikwar.nxtbuz.ui.list

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.BusArrival
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.BusLoad
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.BusType
import kotlinx.android.synthetic.main.item_bus_arrival_compact.view.*

@ListItem(layoutResId = R.layout.item_bus_arrival_compact)
class BusArrivalCompactItem(
    private val busStopCode: String,
    private val busArrival: BusArrival,
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
                busArrival.arrivals.nextArrivingBus.let {
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
                if (busArrival.arrivals.followingArrivingBusList.isNotEmpty()) {
                    busArrival.arrivals.followingArrivingBusList[0].let {
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
                if (busArrival.arrivals.followingArrivingBusList.size >= 2) {
                    busArrival.arrivals.followingArrivingBusList[1].let {
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

        val busInfo =
            busArrival.operator +
                    "  •  ${busArrival.stopSequence} bus stops" +
                    "  •  ${busArrival.distance}KM" +
                    "\n${
                    if (busArrival.direction == 1)
                        activity.getString(R.string.bus_direction_1)
                    else 
                        activity.getString(R.string.bus_direction_2)
                    }" +
                    "${busArrival.originStopDescription}"

        view.parentCv.setOnClickListener { onBusServiceClicked(busArrival.serviceNumber) }

        view.serviceNumberTv.text = busArrival.serviceNumber
        view.busInfoTv.text = busInfo

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

        if (busArrival.starred) {
            view.starIv.setImageResource(R.drawable.ic_round_star_24)
        } else {
            view.starIv.setImageResource(R.drawable.ic_round_star_border_24)
        }

        view.starIv.setOnClickListener {
            view.starIv.toggleStar()
            onStarToggle(busStopCode, busArrival)
        }
    }
}