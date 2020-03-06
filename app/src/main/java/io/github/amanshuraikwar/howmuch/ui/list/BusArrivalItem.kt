package io.github.amanshuraikwar.howmuch.ui.list

import android.content.res.ColorStateList
import android.view.View
import androidx.annotation.ColorInt
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.model.Arrivals
import io.github.amanshuraikwar.howmuch.data.model.BusArrival
import io.github.amanshuraikwar.howmuch.data.model.BusLoad
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.item_bus_arrival.view.*

@ListItem(layoutResId = R.layout.item_bus_arrival)
class BusArrivalItem(
    private val busArrival: BusArrival,
    @ColorInt private val colorControlNormal: Int,
    @ColorInt private val colorControlActivated: Int
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {
        view.serviceNumberTv.text = busArrival.serviceNumber
        view.busInfoTv.text = "${busArrival.operator}  •  ${busArrival.destinationStopDescription}"
        view.busRouteTv.text = "${busArrival.stopSequence}th  •  ${busArrival.distance}KM"
        when (busArrival.arrivals) {
            is Arrivals.Arriving -> {
                busArrival.arrivals.arrivingBusList[0].let {
                    view.nextDepartureTv.text = it.arrival
                    when (it.load) {
                        BusLoad.SEA -> {
                            ColorStateList.valueOf(colorControlActivated).run {
                                view.crowdedIv1.imageTintList = this
                            }
                            ColorStateList.valueOf(colorControlNormal).run {
                                view.crowdedIv2.imageTintList = this
                                view.crowdedIv3.imageTintList = this
                            }
                        }
                        BusLoad.SDA -> {
                            ColorStateList.valueOf(colorControlActivated).run {
                                view.crowdedIv1.imageTintList = this
                                view.crowdedIv2.imageTintList = this
                            }
                            ColorStateList.valueOf(colorControlNormal).run {
                                view.crowdedIv3.imageTintList = this
                            }
                        }
                        BusLoad.LSD -> {
                            ColorStateList.valueOf(colorControlNormal).run {
                                view.crowdedIv1.imageTintList = this
                                view.crowdedIv2.imageTintList = this
                                view.crowdedIv3.imageTintList = this
                            }
                        }
                    }
                }
            }
            is Arrivals.NotOperating -> {
                view.nextDepartureTv.text = "Not Opr"
                ColorStateList.valueOf(colorControlNormal).run {
                    view.crowdedIv1.imageTintList = this
                    view.crowdedIv2.imageTintList = this
                    view.crowdedIv3.imageTintList = this
                }

            }
        }
    }
}