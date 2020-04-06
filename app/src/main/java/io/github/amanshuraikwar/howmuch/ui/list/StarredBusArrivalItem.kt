package io.github.amanshuraikwar.howmuch.ui.list

import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.user.StarredBusArrival
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.item_starred_bus_arrival.view.*

@ListItem(layoutResId = R.layout.item_starred_bus_arrival)
class StarredBusArrivalItem(
    private val starredBusArrival: StarredBusArrival.Arriving
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {
        view.busStopDescriptionTv.text = starredBusArrival.busStopDescription
        view.serviceNumberTv.text = starredBusArrival.busServiceNumber
        view.nextDeparture1Tv.text = starredBusArrival.arrivingBus.arrival
    }
}