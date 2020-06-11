package io.github.amanshuraikwar.nxtbuz.ui.list

import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.StarredBusArrival
import kotlinx.android.synthetic.main.item_starred_bus_arrival.view.*
import kotlinx.android.synthetic.main.item_starred_bus_arrival.view.nextDeparture1Tv
import kotlinx.android.synthetic.main.item_starred_bus_arrival.view.parentCv
import kotlinx.android.synthetic.main.item_starred_bus_arrival.view.serviceNumberTv
import kotlinx.android.synthetic.main.item_starred_bus_arrival_compact_small_error.view.*

@ListItem(layoutResId = R.layout.item_starred_bus_arrival)
class StarredBusArrivalItem(
    val starredBusArrival: StarredBusArrival,
    private val onClicked: (busStopCode: String, busServiceNumber: String) -> Unit,
    private val onLongClick: (busStopCode: String, busServiceNumber: String) -> Unit
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {
        view.parentCv.setOnClickListener {
            onClicked(starredBusArrival.busStopCode, starredBusArrival.busServiceNumber)
        }
        view.parentCv.setOnLongClickListener {
            onLongClick(starredBusArrival.busStopCode, starredBusArrival.busServiceNumber)
            true
        }
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