package io.github.amanshuraikwar.nxtbuz.listitem

import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.common.model.arrival.BusArrivals
import io.github.amanshuraikwar.nxtbuz.common.model.StarredBusArrival
import kotlinx.android.synthetic.main.item_starred_bus_arrival_compact_small_error.view.*

@ListItem(layoutResName = "item_starred_bus_arrival_compact_small_error")
class StarredBusArrivalCompactSmallErrorItem(
    val busArrival: StarredBusArrival,
    private val onLongClick: (busStopCode: String, busServiceNumber: String) -> Unit
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {

        view.parentCv.setOnLongClickListener {
            onLongClick(busArrival.busStopCode, busArrival.busServiceNumber)
            true
        }

        view.serviceNumberTv.text = busArrival.busServiceNumber

        view.nextDeparture1Tv.text = when(val arrivals = busArrival.busArrivals) {
            is BusArrivals.Arriving -> {
                "${arrivals.nextArrivingBus.arrival}"
            }
            is BusArrivals.DataNotAvailable -> {
                "NO DATA"
            }
            is BusArrivals.NotOperating -> {
                "NOT OPERATING"
            }
            is BusArrivals.Error -> {
                "ERROR"
            }
        }
    }
}