package io.github.amanshuraikwar.nxtbuz.busstop.ui

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.android.support.DaggerFragment
import io.github.amanshuraikwar.multiitemadapter.MultiItemAdapter
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.common.util.viewModelProvider
import io.github.amanshuraikwar.nxtbuz.listitem.RecyclerViewTypeFactoryGenerated
import kotlinx.android.synthetic.main.fragment_bus_stops.*
import javax.inject.Inject

class BusStopsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: BusStopsViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private var adapter: MultiItemAdapter<RecyclerViewTypeFactoryGenerated>? = null

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bus_stops, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        itemsRv.layoutManager = LinearLayoutManager(requireActivity())
        setupViewModel()
        setupBottomSheet()
    }

    private fun setupViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.listItems.observe(
            viewLifecycleOwner,
            Observer { listItems ->
                val layoutState = itemsRv.layoutManager?.onSaveInstanceState()
                adapter =
                    MultiItemAdapter(
                        requireActivity(),
                        RecyclerViewTypeFactoryGenerated(),
                        listItems
                    )
                itemsRv.layoutManager?.onRestoreInstanceState(layoutState)
                itemsRv.adapter = adapter ?: return@Observer
                bottomSheet.post {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomSheetBehavior.isHideable = false
                }
            }
        )
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.halfExpandedRatio = 0.5f
        bottomSheetBehavior.peekHeight =
            Point().let { requireActivity().windowManager.defaultDisplay.getSize(it); it.y } / 3
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }
}
