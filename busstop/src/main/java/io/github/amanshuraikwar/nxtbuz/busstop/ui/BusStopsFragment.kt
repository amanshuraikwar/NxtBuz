package io.github.amanshuraikwar.nxtbuz.busstop.ui

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.android.support.DaggerFragment
import io.github.amanshuraikwar.multiitemadapter.MultiItemAdapter
import io.github.amanshuraikwar.nxtbuz.busstop.R
import io.github.amanshuraikwar.nxtbuz.busstop.ui.BusStopsScreenState.*
import io.github.amanshuraikwar.nxtbuz.common.util.lerp
import io.github.amanshuraikwar.nxtbuz.common.util.viewModelProvider
import io.github.amanshuraikwar.nxtbuz.listitem.RecyclerViewTypeFactoryGenerated
import kotlinx.android.synthetic.main.fragment_bus_stops.*
import kotlinx.android.synthetic.main.layout_error_bottom_sheet.*
import kotlinx.android.synthetic.main.layout_loading_bottom_sheet.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject

class BusStopsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: BusStopsViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var errorBottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var loadingBottomSheetBehaviour: BottomSheetBehavior<View>

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
        setupItemListUi()
        setupErrorUi()
        setupLoadingUi()
        setupViewModel()
    }

    private fun setupItemListUi() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.halfExpandedRatio = 0.5f
        bottomSheetBehavior.peekHeight =
            Point().let { requireActivity().windowManager.defaultDisplay.getSize(it); it.y } / 3
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    viewModel.updateBottomSheetSlideOffset(slideOffset)
                    bottomSheetHandle.alpha =
                        lerp(
                            1f, 0f, 0f, 1f, slideOffset
                        )
                    bottomSheetHandle.alpha =
                        lerp(
                            1f, 0f, 0f, 1f, slideOffset
                        )
                    bottomSheetBgView.update(slideOffset)
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    // do nothing
                }
            }
        )
    }

    private fun setupErrorUi() {
        errorBottomSheetBehavior = BottomSheetBehavior.from(errorCl)
        errorBottomSheetBehavior.isHideable = true
        errorBottomSheetBehavior.isFitToContents = true
        errorBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        errorRetryBtn.setOnClickListener {
            viewModel.fetchBusStops()
        }
    }

    private fun setupLoadingUi() {
        loadingBottomSheetBehaviour = BottomSheetBehavior.from(loadingLl)
        loadingBottomSheetBehaviour.isHideable = true
        loadingBottomSheetBehaviour.isFitToContents = true
        loadingBottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun setupViewModel() {
        viewModel = viewModelProvider(viewModelFactory)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.busStopScreenState.collect { screenState ->
                when (screenState) {
                    is Loading -> {
                        hideError()
                        showLoading(screenState.loadingTitle)
                    }
                    is Success -> {
                        hideLoading()
                        adapter =
                            MultiItemAdapter(
                                requireActivity(),
                                RecyclerViewTypeFactoryGenerated(),
                                screenState.itemList
                            )
                        itemsRv.adapter = adapter ?: return@collect
                        bottomSheet.post {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            bottomSheetBehavior.isHideable = false
                        }
                    }
                    is Failed -> {
                        hideLoading()
                        showError(screenState.error)
                    }
                }
                delay(300)
            }
        }
    }

    private suspend fun showLoading(
        @StringRes loadingTitle: Int
    ) = suspendCancellableCoroutine<Unit> {

        loadingTitleTb.setText(loadingTitle)

        if (loadingBottomSheetBehaviour.state == BottomSheetBehavior.STATE_COLLAPSED) {
            loadingBottomSheetBehaviour.isHideable = false
            it.resumeWith(Result.success(Unit))
            return@suspendCancellableCoroutine
        }

        loadingBottomSheetBehaviour.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        loadingBottomSheetBehaviour.isHideable = false
                        loadingBottomSheetBehaviour.removeBottomSheetCallback(this)
                        it.resumeWith(Result.success(Unit))
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // do nothing
                }
            }
        )

        loadingLl.post {
            loadingBottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private suspend fun hideLoading() = suspendCancellableCoroutine<Unit> {

        if (loadingBottomSheetBehaviour.state == BottomSheetBehavior.STATE_HIDDEN) {
            it.resumeWith(Result.success(Unit))
            return@suspendCancellableCoroutine
        }

        loadingBottomSheetBehaviour.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        loadingBottomSheetBehaviour.removeBottomSheetCallback(this)
                        it.resumeWith(Result.success(Unit))
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // do nothing
                }
            }
        )

        loadingLl.post {
            loadingBottomSheetBehaviour.isHideable = true
            loadingBottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private suspend fun showError(
        error: Error
    ) = suspendCancellableCoroutine<Unit> {

        errorTitleTv.setText(error.errorTitle)
        errorDescriptionTv.setText(error.errorDescription)

        if (errorBottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            errorBottomSheetBehavior.isHideable = false
            it.resumeWith(Result.success(Unit))
            return@suspendCancellableCoroutine
        }

        errorBottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        errorBottomSheetBehavior.isHideable = false
                        errorBottomSheetBehavior.removeBottomSheetCallback(this)
                        it.resumeWith(Result.success(Unit))
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // do nothing
                }
            }
        )

        errorCl.post {
            errorBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private suspend fun hideError() = suspendCancellableCoroutine<Unit> {

        if (errorBottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            it.resumeWith(Result.success(Unit))
            return@suspendCancellableCoroutine
        }

        errorBottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        errorBottomSheetBehavior.removeBottomSheetCallback(this)
                        it.resumeWith(Result.success(Unit))
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // do nothing
                }
            }
        )

        errorCl.post {
            errorBottomSheetBehavior.isHideable = true
            errorBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }
}
