package io.github.amanshuraikwar.nxtbuz.listitem

import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.common.model.BusArrivalUpdate
import kotlinx.android.synthetic.main.item_bus_route_next.view.*

@ListItem(layoutResName = "item_bus_route_previous")
class BusRoutePreviousItem(
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