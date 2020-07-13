package io.github.amanshuraikwar.nxtbuz.ui.list

import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.busroute.domain.BusArrivalUpdate
import kotlinx.android.synthetic.main.item_bus_route_next.view.*

@ListItem(layoutResId = R.layout.item_bus_route_next)
class BusRouteNextItem(
    busStopCode: String,
    busStopDescription: String,
    position: Position,
    arrivals: List<String> = emptyList(),
    lastUpdatedAt: String = "",
    onGoToBusStopClick: (busStopCode: String) -> Unit,
    onClick: (busStopCode: String) -> Unit,
) : BusRouteItem(
    busStopCode,
    busStopDescription,
    position,
    arrivals,
    lastUpdatedAt,
    onGoToBusStopClick = onGoToBusStopClick,
    onClick = onClick
), RecyclerViewListItem {
    override fun bind(view: View, activity: FragmentActivity) {
        bindForPosition(view)
        bindBusStop(view)
        view.parentCl.setOnClickListener {
            updateBusArrivals(BusArrivalUpdate.loading(busStopCode))
            updateBusArrivals()
            onClick(busStopCode)
        }
    }
}