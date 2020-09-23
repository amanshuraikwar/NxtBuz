package io.github.amanshuraikwar.nxtbuz.listitem

import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.common.model.BusService
import kotlinx.android.synthetic.main.item_bus_route_header.view.*

@ListItem(layoutResName = "item_search_bus_service")
class SearchBusServiceItem(
    private val busService: BusService,
    private val onClick: (BusService) -> Unit,
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {
        view.serviceNumberTv.text = busService.busServiceNumber
        view.destinationTv.text =
            "${busService.originBusStopDescription} ${activity.getString(R.string.origin_destination)} ${busService.destinationBusStopDescription}"
        view.destinationTv.isSelected = true
        view.infoTv.text = "${busService.numberOfBusStops} Stops  •  ${busService.distance} KM"
        view.setOnClickListener { onClick(busService) }
    }
}