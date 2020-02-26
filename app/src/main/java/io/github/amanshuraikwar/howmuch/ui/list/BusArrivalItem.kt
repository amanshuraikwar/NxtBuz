package io.github.amanshuraikwar.howmuch.ui.list

import android.view.View
import androidx.annotation.DrawableRes
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.model.BusArrival
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.item_bus_arrival.view.*
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.temporal.ChronoUnit

@ListItem(layoutResId = R.layout.item_bus_arrival)
class BusArrivalItem(
    private val busArrival: BusArrival,
    @DrawableRes private val iconResId: Int = R.drawable.ic_round_info_24
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {
        view.serviceNumberTv.text = busArrival.serviceNumber
        //view.stopCodeTv.text = "${busArrival.operator}"
        //view.iconIv.setImageResource(iconResId)
        view.nextDepartureTv.text =
            busArrival.arrivals
                .filter { it.estimatedArrival.isNotEmpty() }
                .takeIf { it.isNotEmpty() }
                ?.get(0)
                ?.let { "${ChronoUnit.MINUTES.between(OffsetDateTime.now(), OffsetDateTime.parse(it.estimatedArrival))} min" }

    }
}