package io.github.amanshuraikwar.howmuch.ui.busstop

import android.content.res.ColorStateList
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.model.BusStop
import io.github.amanshuraikwar.howmuch.domain.result.EventObserver
import io.github.amanshuraikwar.howmuch.ui.list.BusArrivalCompactItem
import io.github.amanshuraikwar.howmuch.ui.list.RecyclerViewTypeFactoryGenerated
import io.github.amanshuraikwar.howmuch.util.viewModelProvider
import io.github.amanshuraikwar.multiitemadapter.MultiItemAdapter
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.activity_bus_stop.*
import javax.inject.Inject

class BusStopActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: BusStopViewModel

    @ColorInt
    private var colorControlNormal: Int = 0

    @ColorInt
    private var colorControlActivated: Int = 0

    private lateinit var updateAvd: Animatable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_stop)
        val busStop: BusStop = intent.getParcelableExtra("busStop") ?: return
        setupViewModel(busStop)
        itemsRv.layoutManager = LinearLayoutManager(this)
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorControlNormal, typedValue, true)
        colorControlNormal = ContextCompat.getColor(this, typedValue.resourceId)
        colorControlActivated = ContextCompat.getColor(this, R.color.blue)
        toolbar.title = busStop.description
        toolbar.setNavigationOnClickListener { finish() }
        updateAvd = updateIv.drawable as Animatable
    }

    private fun setupViewModel(busStop: BusStop) {
        viewModel = viewModelProvider(viewModelFactory) {
            this.busStop = busStop
        }

        viewModel.arrivals.observe(
            this,
            Observer {
                val listItems = mutableListOf<RecyclerViewListItem>()
                it.forEach {
                    listItems.add(
                        BusArrivalCompactItem(it)
                    )
                }
                val x = itemsRv.layoutManager?.onSaveInstanceState()
                val adapter =
                    MultiItemAdapter(this, RecyclerViewTypeFactoryGenerated(), listItems)
                itemsRv.adapter = adapter
                itemsRv.layoutManager?.onRestoreInstanceState(x)
                updateIv.imageTintList = ColorStateList.valueOf(colorControlActivated)
                updateAvd.start()
            }
        )

        viewModel.error.observe(
            this,
            EventObserver {
                updateIv.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this@BusStopActivity,
                        R.color.color_error
                    )
                )
            }
        )
    }
}