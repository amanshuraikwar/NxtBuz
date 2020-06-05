package io.github.amanshuraikwar.nxtbuz.ui.list

import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.StarredBusArrival
import kotlinx.android.synthetic.main.item_starred_bus_arrival_compact_small_error.view.*

@ListItem(layoutResId = R.layout.item_starred_bus_arrival_compact_small_error)
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

        view.nextDeparture1Tv.text = when(val arrivals = busArrival.arrivals) {
            is Arrivals.Arriving -> {
                arrivals.nextArrivingBus.arrival
            }
            is Arrivals.DataNotAvailable -> {
                "NO DATA"
            }
            is Arrivals.NotOperating -> {
                "NOT OPERATING"
            }
        }
    }
}