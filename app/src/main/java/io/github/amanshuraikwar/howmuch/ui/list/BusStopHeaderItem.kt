package io.github.amanshuraikwar.howmuch.ui.list

import android.view.View
import androidx.annotation.DrawableRes
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.model.BusStop
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.item_bus_stop_header.view.*
import kotlinx.android.synthetic.main.item_info_small.view.iconIv
import kotlinx.android.synthetic.main.item_info_small.view.stopNameTv

@ListItem(layoutResId = R.layout.item_bus_stop_header)
class BusStopHeaderItem(
    private val busStop: BusStop,
    @DrawableRes private val iconResId: Int = R.drawable.ic_round_info_24
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {
        view.stopNameTv.text = busStop.description
        view.stopCodeTv.text = "${busStop.roadName} • ${busStop.code}"
        view.iconIv.setImageResource(iconResId)
    }
}