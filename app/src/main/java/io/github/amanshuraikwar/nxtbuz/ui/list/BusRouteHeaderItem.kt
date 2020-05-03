package io.github.amanshuraikwar.nxtbuz.ui.list

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.R
import kotlinx.android.synthetic.main.item_bus_route_header.view.*

@ListItem(layoutResId = R.layout.item_bus_route_header)
class BusRouteHeaderItem(
    private val busStopCode: String,
    private val busServiceNumber: String,
    private val busStopDescription: String,
    private val destinationBusStopDescription: String,
    private val originBusStopDescription: String,
    private var starred: Boolean?,
    private val onStarToggle: (busStopCode: String, busServiceNumber: String) -> Unit
) : RecyclerViewListItem {

    private fun AppCompatImageView.toggleStar() {
        if (starred == true) {
            starred = false
            setImageResource(R.drawable.ic_round_star_border_24)
        } else {
            starred = true
            setImageResource(R.drawable.ic_round_star_24)
        }
    }

    override fun bind(view: View, activity: FragmentActivity) {

        view.serviceNumberTv.text = busServiceNumber
        view.busStopDescriptionTv.text = busStopDescription
        view.infoTv.text =
            """$originBusStopDescription ${activity.getString(R.string.origin_destination)} $destinationBusStopDescription"""
        view.infoTv.isSelected = true
        when (starred) {
            null -> {
                view.starIv.visibility = View.GONE
            }
            true -> {
                view.starIv.visibility = View.VISIBLE
                view.starIv.setImageResource(R.drawable.ic_round_star_24)
            }
            else -> {
                view.starIv.visibility = View.VISIBLE
                view.starIv.setImageResource(R.drawable.ic_round_star_border_24)
            }
        }

        view.starIv.setOnClickListener {
            view.starIv.toggleStar()
            onStarToggle(busStopCode, busServiceNumber)
        }
    }
}