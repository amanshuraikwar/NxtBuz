package io.github.amanshuraikwar.nxtbuz.busstop.arrivals

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
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.common.util.viewModelProvider
import kotlinx.coroutines.launch
import javax.inject.Inject

class BusStopArrivalsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: BusStopArrivalsViewModel

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
                        //BusStopArrivalItems(viewModel)
                    }
                }
            }
        }
    }

    private fun getBusStop(): BusStop? {
        return arguments?.getParcelable("busStop")
    }

    private fun setupViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.busStopArrivalsScreenState.collect { screenState ->
//                when (screenState) {
//                    is Success -> {
//                        nxtBuzBottomSheet.hideError()
//                        if (nxtBuzBottomSheet.isItemListVisible()) {
//                            nxtBuzBottomSheet.updateItemList(
//                                requireActivity(),
//                                screenState.itemList,
//                                RecyclerViewTypeFactoryGenerated()
//                            )
//                        } else {
//                            nxtBuzBottomSheet.showItemList(
//                                requireActivity(),
//                                screenState.itemList,
//                                RecyclerViewTypeFactoryGenerated()
//                            )
//                        }
//                    }
//                    is Failed -> {
//                        nxtBuzBottomSheet.hideItemList()
//                        nxtBuzBottomSheet.showError(screenState.error)
//                    }
//                }
//                delay(300)
//            }
        }
        //viewModel.init(getBusStop() ?: return)
    }


}
