package io.github.amanshuraikwar.nxtbuz.busstop.arrivals

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.android.support.DaggerFragment
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.BusStopArrivalsScreenState.*
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.common.util.viewModelProvider
import kotlinx.android.synthetic.main.fragment_bus_stops.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class BusStopArrivalsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: BusStopArrivalsViewModel

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bus_stop_arrivals, null)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        nxtBuzBottomSheet.setupItemListUi(requireActivity()) { slideOffset ->
            viewModel.updateBottomSheetSlideOffset(slideOffset)
        }
        nxtBuzBottomSheet.setupErrorUi onRetry@{
            viewModel.init(
                busStop = getBusStop() ?: return@onRetry
            )
        }
        nxtBuzBottomSheet.setupLoadingUi()
        setupViewModel()
    }

    private fun getBusStop(): BusStop? {
        return arguments?.getParcelable("busStop")
    }

    private fun setupViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.busStopArrivalsScreenState.collect { screenState ->
                when (screenState) {
//                    is Loading -> {
//                        nxtBuzBottomSheet.hideError()
//                        nxtBuzBottomSheet.showLoading(screenState.loadingTitle)
//                    }
                    is Success -> {
                        nxtBuzBottomSheet.hideError()
                        if (nxtBuzBottomSheet.isItemListVisible()) {
                            nxtBuzBottomSheet.updateItemList(
                                requireActivity(),
                                screenState.itemList
                            )
                        } else {
                            nxtBuzBottomSheet.showItemList(
                                requireActivity(),
                                screenState.itemList
                            )
                        }
                    }
                    is Failed -> {
                        nxtBuzBottomSheet.hideItemList()
                        nxtBuzBottomSheet.showError(screenState.error)
                    }
//                    is Finish -> {
//                        nxtBuzBottomSheet.hideItemList()
//                        viewModel.onFinish(screenState.toBusStop)
//                    }
                }
                delay(300)
            }
        }
        viewModel.init(getBusStop() ?: return)
    }


}
