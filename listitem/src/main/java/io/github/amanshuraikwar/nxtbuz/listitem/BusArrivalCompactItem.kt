package io.github.amanshuraikwar.nxtbuz.listitem

import android.annotation.SuppressLint
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.common.model.arrival.ArrivingBus
import io.github.amanshuraikwar.nxtbuz.common.model.BusLoad
import io.github.amanshuraikwar.nxtbuz.common.model.BusType
import kotlinx.android.synthetic.main.item_bus_arrival_compact.view.*

@ListItem(layoutResName = "item_bus_arrival_compact")
class BusArrivalCompactItem(
    val busStopCode: String,
    val busServiceNumber: String,
    //private val nextBusArrival: String,
    //private val destinationBusStopDescription: String,
    arrivingBus: ArrivingBus? = null,
    //    private var nextDeparture1Tv: String = "N/A"
//    private var crowdedIv1: Int = R.drawable.ic_round_cloud_off_24
//    private var wheelChairAccessIv1: Int = R.drawable.ic_round_cloud_off_24
//    private var busTypeIv1: Int = R.drawable.ic_round_cloud_off_24
    private var starred: Boolean,
    private val onStarToggle: (busStopCode: String, busServiceNumber: String) -> Unit,
    private val onClicked: (busServiceNumber: String) -> Unit
) : RecyclerViewListItem {

    private val crowdedIv: Int = when (arrivingBus?.load) {
        BusLoad.SEA -> {
            R.drawable.ic_bus_load_1_16
        }
        BusLoad.SDA -> {
            R.drawable.ic_bus_load_2_16
        }
        BusLoad.LSD -> {
            R.drawable.ic_bus_load_3_16
        }
        else -> {
            0
        }
    }

    private val wheelChairAccessIv: Int = when {
        arrivingBus == null -> {
            0
        }
        arrivingBus.wheelchairAccess -> {
            R.drawable.ic_accessible_16
        }
        else -> {
            R.drawable.ic_not_accessible_16
        }
    }

    private val busTypeIv: Int = when (arrivingBus?.type) {
        BusType.DD -> R.drawable.ic_bus_dd_16
        BusType.BD -> R.drawable.ic_bus_feeder_16
        else -> R.drawable.ic_bus_normal_16
    }

    private val nextBusArrival: String = when {
        arrivingBus == null -> {
            "Fetching arrivals..."
        }
        arrivingBus.arrival == 0 -> {
            "Arriving Now"
        }
        else -> {
            "${arrivingBus.arrival} mins"
        }
    }

    private val destinationBusStopDescription: String =
        arrivingBus?.destination?.busStopDescription ?: ""

    //                if ((busArrival.arrivals as Arrivals.Arriving).followingArrivingBusList.isNotEmpty()) {
//                    (busArrival.arrivals as Arrivals.Arriving).followingArrivingBusList[0].let {
//                        nextDeparture2Tv = it.arrival
//                        crowdedIv2 =
//                            when (it.load) {
//                                BusLoad.SEA -> {
//                                    R.drawable.ic_load_1_24
//                                }
//                                BusLoad.SDA -> {
//                                    R.drawable.ic_load_2_24
//                                }
//                                BusLoad.LSD -> {
//                                    R.drawable.ic_load_3_24
//                                }
//                            }
//                        wheelChairAccessIv2 =
//                            if (it.feature == "WAB") {
//                                R.drawable.ic_round_accessible_24
//                            } else {
//                                R.drawable.ic_round_accessible_inactive_24
//                            }
//                        busTypeIv2 =
//                            when (it.type) {
//                                BusType.SD -> R.drawable.ic_round_directions_bus_24
//                                BusType.DD -> R.drawable.ic_bus_dd_24
//                                BusType.BD -> R.drawable.ic_bus_feeder_24
//                            }
//                    }
//                }
//                if ((busArrival.arrivals as Arrivals.Arriving).followingArrivingBusList.size >= 2) {
//                    (busArrival.arrivals as Arrivals.Arriving).followingArrivingBusList[1].let {
//                        nextDeparture3Tv = it.arrival
//                        crowdedIv3 =
//                            when (it.load) {
//                                BusLoad.SEA -> {
//                                    R.drawable.ic_load_1_24
//                                }
//                                BusLoad.SDA -> {
//                                    R.drawable.ic_load_2_24
//                                }
//                                BusLoad.LSD -> {
//                                    R.drawable.ic_load_3_24
//                                }
//                            }
//                        wheelChairAccessIv3 =
//                            if (it.feature == "WAB") {
//                                R.drawable.ic_round_accessible_24
//                            } else {
//                                R.drawable.ic_round_accessible_inactive_24
//                            }
//                        busTypeIv3 =
//                            when (it.type) {
//                                BusType.SD -> R.drawable.ic_round_directions_bus_24
//                                BusType.DD -> R.drawable.ic_bus_dd_24
//                                BusType.BD -> R.drawable.ic_bus_feeder_24
//                            }
//                    }
//                }
//            }
//            is Arrivals.NotOperating,
//            is Arrivals.DataNotAvailable -> {
//                throw IllegalArgumentException("Bus arrivals cannot be error.")
//            }
//        }
//}

    private fun AppCompatImageView.toggleStar() {
        if (starred) {
            starred = false
            setImageResource(R.drawable.ic_round_star_border_24)
        } else {
            starred = true
            setImageResource(R.drawable.ic_round_star_24)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun bind(view: View, activity: FragmentActivity) {

        view.parentCv.setOnClickListener { onClicked(busServiceNumber) }

        view.serviceNumberTv.text = busServiceNumber
        view.busInfoTv.text = destinationBusStopDescription

        if (starred) {
            view.starIv.setImageResource(R.drawable.ic_round_star_24)
        } else {
            view.starIv.setImageResource(R.drawable.ic_round_star_border_24)
        }

        view.starIv.setOnClickListener {
            view.starIv.toggleStar()
            onStarToggle(busStopCode, busServiceNumber)
        }

        view.nextDepartureTv.text = nextBusArrival

        view.crowdedIv.setImageResource(crowdedIv)
        view.wheelChairAccessIv.setImageResource(wheelChairAccessIv)
        view.busTypeIv.setImageResource(busTypeIv)
    }
}