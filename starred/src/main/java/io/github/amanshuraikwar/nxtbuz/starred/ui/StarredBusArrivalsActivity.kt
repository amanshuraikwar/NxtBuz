package io.github.amanshuraikwar.nxtbuz.starred.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerAppCompatActivity
import io.github.amanshuraikwar.multiitemadapter.MultiItemAdapter
import io.github.amanshuraikwar.nxtbuz.common.model.EventObserver
import io.github.amanshuraikwar.nxtbuz.common.util.viewModelProvider
import io.github.amanshuraikwar.nxtbuz.listitem.HeaderItem
import io.github.amanshuraikwar.nxtbuz.listitem.RecyclerViewTypeFactoryGenerated
import io.github.amanshuraikwar.nxtbuz.listitem.StarredBusArrivalCompactSmallErrorItem
import io.github.amanshuraikwar.nxtbuz.listitem.StarredBusArrivalCompactSmallItem
import io.github.amanshuraikwar.nxtbuz.starred.R
import io.github.amanshuraikwar.nxtbuz.starred.ui.options.StarredBusArrivalOptionsDialogFragment
import kotlinx.android.synthetic.main.activity_starred_bus_arrivals.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class StarredBusArrivalsActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var adapter: MultiItemAdapter<RecyclerViewTypeFactoryGenerated>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starred_bus_arrivals)
        itemsRv.layoutManager = LinearLayoutManager(this)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        setupViewModel()
    }

    private fun setupViewModel() {

        val viewModel = viewModelProvider<StarredBusArrivalsViewModel>(viewModelFactory)

        viewModel.listItems.observe(
            this,
            Observer { listItems ->
                val layoutState = itemsRv.layoutManager?.onSaveInstanceState()
                adapter =
                    MultiItemAdapter(this, RecyclerViewTypeFactoryGenerated(), listItems)
                itemsRv.layoutManager?.onRestoreInstanceState(layoutState)
                itemsRv.adapter = adapter
            }
        )

        viewModel.starredBusArrivalClicked.observe(
            this,
            EventObserver {
                setResult(
                    Activity.RESULT_OK,
                    Intent().putExtra(
                        KEY_STARRED_BUS_ARRIVAL_CLICKED,
                        it
                    )
                )
                finish()
            }
        )

        viewModel.starredBusArrivalOptionsDialog.observe(
            this,
            EventObserver { (busStop, busServiceNumber) ->
                StarredBusArrivalOptionsDialogFragment(
                    busStop,
                    busServiceNumber
                ).show(
                    supportFragmentManager, "starred-bus-arrival-options"
                )
            }
        )

        viewModel.remove.observe(
            this,
            EventObserver { (busStop, busServiceNumber) ->

                adapter
                    .prepareRemove { item ->
                        if (item is StarredBusArrivalCompactSmallItem) {
                            return@prepareRemove item.busArrival.busStopCode == busStop.code
                                    && item.busArrival.busServiceNumber == busServiceNumber
                        }
                        if (item is StarredBusArrivalCompactSmallErrorItem) {
                            return@prepareRemove item.busArrival.busStopCode == busStop.code
                                    && item.busArrival.busServiceNumber == busServiceNumber
                        }
                        return@prepareRemove false
                    }
                    ?.alsoRemove { currentIndex ->
                        if (previousItem() is HeaderItem
                            && (nextItem() is HeaderItem || isCurrentItemLast())
                        ) {
                            currentIndex - 1
                        } else {
                            -1
                        }
                    }
                    ?.doItNow()

                if (adapter.items.size == 0) {
                    finish()
                }
            }
        )
    }

    companion object {
        const val KEY_STARRED_BUS_ARRIVAL_CLICKED = "starred_bus_arrival_clicked"
    }
}