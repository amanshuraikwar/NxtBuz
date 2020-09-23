package io.github.amanshuraikwar.nxtbuz.listitem

import android.view.View
import io.github.amanshuraikwar.nxtbuz.common.model.BusArrivalUpdate
import kotlinx.android.synthetic.main.item_bus_route_next.view.*
import kotlinx.android.synthetic.main.layout_bus_stop_arrivals.view.*

open class BusRouteItem(
    val busStopCode: String,
    val busStopDescription: String,
    val position: Position,
    var arrivals: List<String> = emptyList(),
    private var lastUpdatedAt: String = "",
    private var onGoToBusStopClick: (busStopCode: String) -> Unit = {},
    protected var onClick: (busStopCode: String) -> Unit = {},
) {

    private var boundView: View? = null

    enum class Position { ORIGIN, DESTINATION, MIDDLE }

    protected fun bindForPosition(view: View) {
        boundView = view
        when (position) {
            Position.ORIGIN -> {
                view.topBar.visibility = View.INVISIBLE
                view.bottomBar.visibility = View.VISIBLE
            }
            Position.DESTINATION -> {
                view.topBar.visibility = View.VISIBLE
                view.bottomBar.visibility = View.INVISIBLE
            }
            Position.MIDDLE -> {
                view.topBar.visibility = View.VISIBLE
                view.bottomBar.visibility = View.VISIBLE
            }
        }

        updateBusArrivals()
    }

    fun updateBusArrivals(busArrivalUpdate: BusArrivalUpdate) {
        arrivals = busArrivalUpdate.arrivalList
        lastUpdatedAt = busArrivalUpdate.lastUpdateAt
    }

    protected fun updateBusArrivals() {
        boundView?.let { view ->
            if (arrivals.isNotEmpty()) {
                view.arrival1Tv.text = arrivals[0]
                if (arrivals.size >= 2) {
                    view.arrival2Tv.text = arrivals[1]
                    view.arrival2Iv.visibility = View.VISIBLE
                    view.arrival2Tv.visibility = View.VISIBLE
                } else {
                    view.arrival2Iv.visibility = View.INVISIBLE
                    view.arrival2Tv.visibility = View.INVISIBLE
                }
                if (arrivals.size == 3) {
                    view.arrival3Tv.text = arrivals[2]
                    view.arrival3Iv.visibility = View.VISIBLE
                    view.arrival3Tv.visibility = View.VISIBLE
                } else {
                    view.arrival3Iv.visibility = View.INVISIBLE
                    view.arrival3Tv.visibility = View.INVISIBLE
                }
                view.radar.visibility = View.VISIBLE
                view.arrivals.visibility = View.VISIBLE
                view.goToBusStopBtn.setOnClickListener { onGoToBusStopClick(busStopCode) }
            } else {
                view.radar.visibility = View.INVISIBLE
                view.arrivals.visibility = View.GONE
            }
            if (lastUpdatedAt.isEmpty()) {
                view.lastUpdatedAtTv.text = "..."
            } else {
                view.lastUpdatedAtTv.text = "Last updated at $lastUpdatedAt"
            }
        }
    }

    protected fun bindBusStop(view: View) {
        view.busStopDescriptionTv.text = busStopDescription
    }
}