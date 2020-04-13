package io.github.amanshuraikwar.nxtbuz.ui.list

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.Arrivals
import io.github.amanshuraikwar.nxtbuz.data.busarrival.model.BusArrival
import kotlinx.android.synthetic.main.item_bus_arrival_error.view.*

@ListItem(layoutResId = R.layout.item_bus_arrival_error)
class BusArrivalErrorItem(
    private val busStopCode: String,
    private val busArrival: BusArrival,
    private val onStarToggle: (busStopCode: String, busArrival: BusArrival) -> Unit
) : RecyclerViewListItem {

    private val busInfo =
        "${busArrival.stopSequence}  •  ${busArrival.distance}KM  •  ${busArrival.direction}\n" +
                when (busArrival.arrivals) {
                    is Arrivals.NotOperating -> "Not operating"
                    is Arrivals.DataNotAvailable -> "Data not available"
                    else -> throw IllegalArgumentException("Bus arrivals cannot be arriving.")
                }

    private fun AppCompatImageView.toggleStar() {
        if (busArrival.starred) {
            busArrival.starred = false
            setImageResource(R.drawable.ic_round_star_border_24)
        } else {
            busArrival.starred = true
            setImageResource(R.drawable.ic_round_star_24)
        }
    }

    override fun bind(view: View, activity: FragmentActivity) {
        view.serviceNumberTv.text = busArrival.serviceNumber
        view.busInfoTv.text = busInfo

        if (busArrival.starred) {
            view.starIv.setImageResource(R.drawable.ic_round_star_24)
        } else {
            view.starIv.setImageResource(R.drawable.ic_round_star_border_24)
        }

        view.starIv.setOnClickListener {
            view.starIv.toggleStar()
            onStarToggle(busStopCode, busArrival)
        }
    }
}