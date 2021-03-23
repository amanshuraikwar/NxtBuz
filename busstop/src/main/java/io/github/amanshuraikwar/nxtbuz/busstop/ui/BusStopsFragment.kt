package io.github.amanshuraikwar.nxtbuz.busstop.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.android.support.DaggerFragment
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.busstop.arrivals.item.BusStopArrivalItems
import io.github.amanshuraikwar.nxtbuz.busstop.ui.BusStopsScreenState.*
import io.github.amanshuraikwar.nxtbuz.busstop.ui.items.BusStopsScreen
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.common.util.viewModelProvider
import io.github.amanshuraikwar.nxtbuz.listitem.RecyclerViewTypeFactoryGenerated
import kotlinx.android.synthetic.main.fragment_bus_stops.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class BusStopsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: BusStopsViewModel

    @ExperimentalMaterialApi
    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupViewModel()
        return ComposeView(requireContext()).apply {
            setContent {
                NxtBuzTheme {
                    ProvideWindowInsets {
                        //BusStopsScreen(vm = viewModel)
                    }
                }
            }
        }
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        nxtBuzBottomSheet.setupItemListUi(requireActivity()) { slideOffset ->
//            viewModel.updateBottomSheetSlideOffset(slideOffset)
//        }
//        nxtBuzBottomSheet.setupErrorUi {
//            viewModel.fetchBusStops()
//        }
//        nxtBuzBottomSheet.setupLoadingUi()
//        setupViewModel()
//    }

    private fun setupViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.busStopScreenState.collect { screenState ->
                when (screenState) {
                    is Loading -> {
                        nxtBuzBottomSheet.hideError()
                        nxtBuzBottomSheet.showLoading(screenState.loadingTitle)
                    }
                    is Success -> {
                        nxtBuzBottomSheet.hideLoading()
                            nxtBuzBottomSheet.showItemList(
                            requireActivity(),
                            screenState.itemList,
                            RecyclerViewTypeFactoryGenerated()
                        )
                    }
                    is Failed -> {
                        nxtBuzBottomSheet.hideLoading()
                        nxtBuzBottomSheet.showError(screenState.error)
                    }
                }
                delay(300)
            }
        }
    }


}
