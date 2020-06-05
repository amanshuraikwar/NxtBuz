package io.github.amanshuraikwar.nxtbuz.ui.starred

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerAppCompatActivity
import io.github.amanshuraikwar.multiitemadapter.MultiItemAdapter
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.domain.result.EventObserver
import io.github.amanshuraikwar.nxtbuz.ui.list.RecyclerViewTypeFactoryGenerated
import io.github.amanshuraikwar.nxtbuz.ui.list.StarredBusArrivalCompactSmallErrorItem
import io.github.amanshuraikwar.nxtbuz.ui.list.StarredBusArrivalCompactSmallItem
import io.github.amanshuraikwar.nxtbuz.ui.starred.options.StarredBusArrivalOptionsDialogFragment
import io.github.amanshuraikwar.nxtbuz.util.viewModelProvider
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
                StarredBusArrivalOptionsDialogFragment(busStop, busServiceNumber).show(
                    supportFragmentManager, "starred-bus-arrival-options"
                )
            }
        )

        viewModel.remove.observe(
            this,
            EventObserver { (busStop, busServiceNumber) ->
                adapter.remove { item ->
                    if (item is StarredBusArrivalCompactSmallItem) {
                        return@remove item.busArrival.busStopCode == busStop.code
                                && item.busArrival.busServiceNumber == busServiceNumber
                    }
                    if (item is StarredBusArrivalCompactSmallErrorItem) {
                        return@remove item.busArrival.busStopCode == busStop.code
                                && item.busArrival.busServiceNumber == busServiceNumber
                    }
                    return@remove false
                }
            }
        )
    }

    companion object {
        const val KEY_STARRED_BUS_ARRIVAL_CLICKED = "starred_bus_arrival_clicked"
    }
}