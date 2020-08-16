package io.github.amanshuraikwar.nxtbuz.ui.list

import android.view.View
import androidx.annotation.DrawableRes
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import kotlinx.android.synthetic.main.item_bus_stop.view.*
import kotlinx.android.synthetic.main.item_info_small.view.iconIv
import kotlinx.android.synthetic.main.item_info_small.view.stopNameTv

@ListItem(layoutResId = R.layout.item_search_bus_stop)
class SearchBusStopItem(
    private val busStop: BusStop,
    @DrawableRes private val iconResId: Int = R.drawable.ic_round_info_24,
    private val onClick: (BusStop) -> Unit,
    private val onGotoClick: (BusStop) -> Unit
) : RecyclerViewListItem {

    private val operatingBuses =
        busStop.operatingBusList
            .map { it.serviceNumber }
            .reduceRight { next, total -> "${if (total.length == 2) "$total  " else if (total.length == 3) "$total " else total}  ${if (next.length == 2) "$next  " else if (next.length == 3) "$next " else next}" }

    override fun bind(view: View, activity: FragmentActivity) {
        view.stopNameTv.text = busStop.description
        view.stopCodeTv.text = "${busStop.roadName} • ${busStop.code}"
        view.iconIv.setImageResource(iconResId)
        view.parentCv.setOnClickListener { onClick(busStop) }
        view.gotoIv.setOnClickListener { onGotoClick(busStop) }
        view.busCodesTv.text = operatingBuses
    }
}