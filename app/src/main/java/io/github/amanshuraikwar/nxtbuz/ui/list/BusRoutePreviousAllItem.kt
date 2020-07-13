package io.github.amanshuraikwar.nxtbuz.ui.list

import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.R
import kotlinx.android.synthetic.main.item_bus_route_previous_all.view.*

@ListItem(layoutResId = R.layout.item_bus_route_previous_all)
class BusRoutePreviousAllItem(
    busStopCode: String,
    busStopDescription: String,
    position: Position,
    onClick: (String) -> Unit
) : BusRouteItem(
    busStopCode,
    busStopDescription,
    position,
    onClick = onClick
), RecyclerViewListItem {
    override fun bind(view: View, activity: FragmentActivity) {
        bindBusStop(view)
        view.parentCl.setOnClickListener { onClick(busStopCode) }
    }
}