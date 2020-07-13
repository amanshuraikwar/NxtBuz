package io.github.amanshuraikwar.nxtbuz.ui.list

import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.R

@ListItem(layoutResId = R.layout.item_bus_route_current)
class BusRouteCurrentItem(
    busStopCode: String,
    busStopDescription: String,
    position: Position,
    arrivals: List<String> = emptyList(),
    lastUpdatedAt: String = "",
    onGoToBusStopClick: (busStopCode: String) -> Unit
) : BusRouteItem(
    busStopCode,
    busStopDescription,
    position,
    arrivals,
    lastUpdatedAt,
    onGoToBusStopClick = onGoToBusStopClick,
), RecyclerViewListItem {
    override fun bind(view: View, activity: FragmentActivity) {
        bindForPosition(view)
        bindBusStop(view)
        view.isSelected = false
    }
}