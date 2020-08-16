package io.github.amanshuraikwar.nxtbuz.ui.list

import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.common.model.BusRouteNode
import kotlinx.android.synthetic.main.item_bus_route_node.view.*

@ListItem(layoutResId = R.layout.item_bus_route_node)
class BusRouteNodeItem(
    private val busRouteNode: BusRouteNode,
    private val first: Boolean = false,
    private val last: Boolean = false,
    private val onBusStopClicked: (busStopCode: String) -> Unit
) : RecyclerViewListItem {

    private val busStopDescription = busRouteNode.busStopDescription

    override fun bind(view: View, activity: FragmentActivity) {

        view.contentCv.setOnClickListener { onBusStopClicked(busRouteNode.busStopCode) }

        view.busStopDescriptionTv.text = busStopDescription

        val busStopInfo = "${busRouteNode.busStopRoadName}  •  " +
                "${busRouteNode.stopSequence}  •  " +
                "${busRouteNode.distance}KM  •  " +
                if (busRouteNode.direction == 1)
                    activity.getString(R.string.bus_direction_1)
                else
                    activity.getString(R.string.bus_direction_2)

        view.busStopInfoTv.text = busStopInfo

        view.lineTopV.visibility = if (first) {
            View.GONE
        } else {
            View.VISIBLE
        }

        view.lineBottomV.visibility = if (last) {
            View.GONE
        } else {
            View.VISIBLE
        }


    }
}