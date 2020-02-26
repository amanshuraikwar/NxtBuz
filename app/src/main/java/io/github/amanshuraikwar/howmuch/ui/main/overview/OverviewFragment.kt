package io.github.amanshuraikwar.howmuch.ui.main.overview

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerFragment
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.model.BusStop
import io.github.amanshuraikwar.howmuch.data.model.Transaction
import io.github.amanshuraikwar.howmuch.domain.result.EventObserver
import io.github.amanshuraikwar.howmuch.ui.busstop.BusStopActivity
import io.github.amanshuraikwar.howmuch.ui.list.BusStopItem
import io.github.amanshuraikwar.howmuch.ui.list.RecyclerViewTypeFactoryGenerated
import io.github.amanshuraikwar.howmuch.util.viewModelProvider
import io.github.amanshuraikwar.multiitemadapter.MultiItemAdapter
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.fragment_overview.*
import javax.inject.Inject


private const val TAG = "OverviewFragment"

class OverviewFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: OverviewViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
    }

    private fun onTransactionClicked(transaction: Transaction) {

    }

    @SuppressLint("SetTextI18n", "InflateParams")
    private fun setupViewModel() {

        requireActivity().let { activity ->

            viewModel = viewModelProvider(viewModelFactory) {
                colorControlNormalResId = TypedValue().let {
                    ContextCompat.getColor(activity, R.color.color_distribution_bar_def)
                }
            }

            viewModel.error.observe(
                this,
                EventObserver {
                    val view = activity.layoutInflater.inflate(R.layout.dialog_error, null)
                    val dialog =
                        MaterialAlertDialogBuilder(activity).setCancelable(false).setView(view)
                            .create()
                    view.findViewById<TextView>(R.id.errorMessageTv).text = it
                    view.findViewById<MaterialButton>(R.id.retryBtn).setOnClickListener {
                        // todo
                        dialog.dismiss()
                    }
                    dialog.show()
                }
            )

            viewModel.busStops.observe(
                this,
                Observer {

                    val listItems = mutableListOf<RecyclerViewListItem>()
                    it.forEach {
                        listItems.add(
                            BusStopItem(
                                it,
                                R.drawable.ic_round_directions_bus_72,
                                ::onBusStopClicked,
                                ::onGotoClicked
                            )
                        )
                    }

                    val adapter =
                        MultiItemAdapter(activity, RecyclerViewTypeFactoryGenerated(), listItems)
                    itemsRv.layoutManager = LinearLayoutManager(activity)
                    itemsRv.adapter = adapter
                }
            )
        }
    }

    private fun onBusStopClicked(busStop: BusStop) {
        startActivity(Intent(activity, BusStopActivity::class.java).putExtra("busStop", busStop))
    }

    private fun onGotoClicked(busStop: BusStop) {
        // todo: think control via settings
//        val gmmIntentUri: Uri = Uri.parse("google.navigation:q=${busStop.latitude},${busStop.longitude}&mode=w")
//        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//        mapIntent.setPackage("com.google.android.apps.maps")
//        startActivity(mapIntent)
        val browserIntent =
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    "https://www.google.com/maps/dir/" +
                            "?api=1" +
                            "&destination=${busStop.latitude},${busStop.longitude}&travelmode=walking"
                )
            )
        startActivity(browserIntent)
    }
}