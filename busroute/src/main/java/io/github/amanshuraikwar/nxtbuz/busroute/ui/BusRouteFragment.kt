package io.github.amanshuraikwar.nxtbuz.busroute.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.android.support.DaggerFragment
import io.github.amanshuraikwar.nxtbuz.busroute.R
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.common.util.viewModelProvider
import io.github.amanshuraikwar.nxtbuz.listitem.RecyclerViewTypeFactoryGenerated
import kotlinx.android.synthetic.main.fragment_bus_route.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class BusRouteFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: BusRouteViewModel

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bus_route, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        nxtBuzBottomSheet.setupItemListUi(requireActivity()) { slideOffset ->
            viewModel.updateBottomSheetSlideOffset(slideOffset)
        }
        nxtBuzBottomSheet.setupErrorUi onRetry@{
            viewModel.init(
                busServiceNumber = getBusServiceNumber() ?: return@onRetry,
                busStop = getBusStop()
            )
        }
        nxtBuzBottomSheet.setupLoadingUi()
        setupViewModel()
    }

    private fun getBusStop(): BusStop? {
        return arguments?.getParcelable("busStop")
    }

    private fun getBusServiceNumber(): String? {
        return arguments?.getString("busServiceNumber")
    }

    private fun setupViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.screenState.collect { screenState ->
                when (screenState) {
                    is BusRouteScreenState.Success -> {
                        nxtBuzBottomSheet.hideError()
                        if (nxtBuzBottomSheet.isItemListVisible()) {
                            nxtBuzBottomSheet.updateItemList(
                                requireActivity(),
                                screenState.itemList,
                                RecyclerViewTypeFactoryGenerated()
                            )
                        } else {
                            nxtBuzBottomSheet.showItemList(
                                requireActivity(),
                                screenState.itemList,
                                RecyclerViewTypeFactoryGenerated()
                            )
                        }
                    }
                    is BusRouteScreenState.Failed -> {
                        nxtBuzBottomSheet.hideItemList()
                        nxtBuzBottomSheet.showError(screenState.error)
                    }
                }
                delay(300)
            }
        }
        viewModel.init(
            busServiceNumber = getBusServiceNumber() ?: return,
            busStop = getBusStop()
        )
    }
}