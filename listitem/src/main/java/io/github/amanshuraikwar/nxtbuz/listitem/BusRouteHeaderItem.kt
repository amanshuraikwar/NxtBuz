package io.github.amanshuraikwar.nxtbuz.listitem

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.item_bus_route_header.view.*

@ListItem(layoutResName = "item_bus_route_header")
class BusRouteHeaderItem(
    val busStopCode: String?,
    val busServiceNumber: String,
    private val totalBusStops: Int,
    private val totalDistance: Double,
    private val destinationBusStopDescription: String,
    private val originBusStopDescription: String,
    var starred: Boolean?,
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
        view.destinationTv.text =
            "$originBusStopDescription ${activity.getString(R.string.origin_destination)} $destinationBusStopDescription"
        view.destinationTv.isSelected = true
        view.infoTv.text = "$totalBusStops Stops  •  $totalDistance KM"
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

        if (busStopCode != null) {

            view.starIv.visibility = View.VISIBLE
            view.starIv.setOnClickListener {
                view.starIv.toggleStar()
                onStarToggle(busStopCode, busServiceNumber)
            }

        } else {
            view.starIv.visibility = View.GONE
        }

    }
}