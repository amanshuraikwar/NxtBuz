package io.github.amanshuraikwar.nxtbuz.ui.list

import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.StarredBusArrival
import kotlinx.android.synthetic.main.item_starred_bus_arrival.view.*

@ListItem(layoutResId = R.layout.item_starred_bus_arrival)
class StarredBusArrivalItem(
    private val starredBusArrival: StarredBusArrival
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {
        view.busStopDescriptionTv.text = starredBusArrival.busStopDescription
        view.serviceNumberTv.text = starredBusArrival.busServiceNumber
        view.nextDeparture1Tv.text = when(val arrivals = starredBusArrival.arrivals) {
            is Arrivals.Arriving -> {
                arrivals.nextArrivingBus.arrival
            }
            is Arrivals.DataNotAvailable -> {
                "N/A"
            }
            is Arrivals.NotOperating -> {
                "N/O"
            }
        }
    }
}