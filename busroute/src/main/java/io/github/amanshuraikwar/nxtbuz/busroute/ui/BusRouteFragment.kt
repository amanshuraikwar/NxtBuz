package io.github.amanshuraikwar.nxtbuz.busroute.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerFragment
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import io.github.amanshuraikwar.nxtbuz.busroute.ui.item.BusRouteItems
import io.github.amanshuraikwar.nxtbuz.common.compose.theme.NxtBuzTheme
import io.github.amanshuraikwar.nxtbuz.common.model.BusStop
import io.github.amanshuraikwar.nxtbuz.common.util.viewModelProvider
import javax.inject.Inject

class BusRouteFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: BusRouteViewModel

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
                        //BusRouteItems(viewModel)
                    }
                }
            }
        }
    }

    private fun getBusStop(): BusStop? {
        return arguments?.getParcelable("busStop")
    }

    private fun getBusServiceNumber(): String? {
        return arguments?.getString("busServiceNumber")
    }

    private fun setupViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewModel.init(
            busServiceNumber = getBusServiceNumber() ?: return,
            busStop = getBusStop()
        )
    }
}