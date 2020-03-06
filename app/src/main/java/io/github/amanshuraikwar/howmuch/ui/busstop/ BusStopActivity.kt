package io.github.amanshuraikwar.howmuch.ui.busstop

import android.os.Bundle
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerAppCompatActivity
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.domain.result.EventObserver
import io.github.amanshuraikwar.howmuch.ui.list.BusArrivalItem
import io.github.amanshuraikwar.howmuch.ui.list.RecyclerViewTypeFactoryGenerated
import io.github.amanshuraikwar.howmuch.util.viewModelProvider
import io.github.amanshuraikwar.multiitemadapter.MultiItemAdapter
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.fragment_overview.*
import javax.inject.Inject

class BusStopActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: BusStopViewModel

    @ColorInt
    private var colorControlNormal: Int = 0

    @ColorInt
    private var colorControlActivated: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.activity_bus_stop)
        setupViewModel()
        itemsRv.layoutManager = LinearLayoutManager(this)
        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.colorControlNormal, typedValue, true)
        colorControlNormal = ContextCompat.getColor(this, typedValue.resourceId)
        colorControlActivated = ContextCompat.getColor(this, R.color.blue)
    }

    private fun setupViewModel() {
        viewModel = viewModelProvider(viewModelFactory) {
            busStop = intent.getParcelableExtra("busStop") ?: return
        }

        viewModel.arrivals.observe(
            this,
            Observer {
                val listItems = mutableListOf<RecyclerViewListItem>()
                it.forEach {
                    listItems.add(
                        BusArrivalItem(
                            it,
                            colorControlNormal,
                            colorControlActivated
                        )
                    )
                }
                val x = itemsRv.layoutManager?.onSaveInstanceState()
                val adapter =
                    MultiItemAdapter(this, RecyclerViewTypeFactoryGenerated(), listItems)
                itemsRv.adapter = adapter
                itemsRv.layoutManager?.onRestoreInstanceState(x)
            }
        )

        viewModel.error.observe(
            this,
            EventObserver {
                val view = layoutInflater.inflate(R.layout.dialog_error, null)
                val dialog =
                    MaterialAlertDialogBuilder(this).setCancelable(false).setView(view)
                        .create()
                view.findViewById<TextView>(R.id.errorMessageTv).text = it
                view.findViewById<MaterialButton>(R.id.retryBtn).setOnClickListener {
                    // todo
                    dialog.dismiss()
                }
                dialog.show()
            }
        )
    }
}