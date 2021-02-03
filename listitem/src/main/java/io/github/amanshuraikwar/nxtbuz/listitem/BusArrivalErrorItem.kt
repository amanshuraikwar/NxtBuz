package io.github.amanshuraikwar.nxtbuz.listitem

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.item_bus_arrival_error.view.*

@ListItem(layoutResName = "item_bus_arrival_error")
class BusArrivalErrorItem(
    val busStopCode: String,
    val busServiceNumber: String,
    private val errorReason: String,
    private var starred: Boolean,
    private val onStarToggle: (busStopCode: String, busServiceNumber: String) -> Unit
) : RecyclerViewListItem {

    private fun AppCompatImageView.toggleStar() {
        if (starred) {
            starred = false
            setImageResource(R.drawable.ic_round_star_border_24)
        } else {
            starred = true
            setImageResource(R.drawable.ic_round_star_24)
        }
    }

    override fun bind(view: View, activity: FragmentActivity) {
        view.serviceNumberTv.text = busServiceNumber
        view.errorReasonTv.text = errorReason

        if (starred) {
            view.starIv.setImageResource(R.drawable.ic_round_star_24)
        } else {
            view.starIv.setImageResource(R.drawable.ic_round_star_border_24)
        }

        view.starIv.setOnClickListener {
            view.starIv.toggleStar()
            onStarToggle(busStopCode, busServiceNumber)
        }
    }
}