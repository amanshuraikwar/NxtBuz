package io.github.amanshuraikwar.nxtbuz.common.view

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.github.amanshuraikwar.multiitemadapter.MultiItemAdapter
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewTypeFactory
import io.github.amanshuraikwar.nxtbuz.common.R
import io.github.amanshuraikwar.nxtbuz.common.model.view.Error
import io.github.amanshuraikwar.nxtbuz.common.util.lerp
import kotlinx.android.synthetic.main.layout_error_bottom_sheet.*
import kotlinx.android.synthetic.main.layout_error_bottom_sheet.view.*
import kotlinx.android.synthetic.main.layout_loading_bottom_sheet.*
import kotlinx.android.synthetic.main.layout_loading_bottom_sheet.view.*
import kotlinx.android.synthetic.main.nxt_buz_bottom_sheet.view.*
import kotlinx.coroutines.suspendCancellableCoroutine

class NxtBuzBottomSheet @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var errorBottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var loadingBottomSheetBehaviour: BottomSheetBehavior<View>

    private var adapter: MultiItemAdapter<RecyclerViewTypeFactory>? = null

    init {
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as? LayoutInflater)
            ?.inflate(R.layout.nxt_buz_bottom_sheet, this)
    }

    fun setupItemListUi(activity: Activity, onBottomSheetSlide: (slideOffset: Float) -> Unit) {
        itemsRv.layoutManager = LinearLayoutManager(activity)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.halfExpandedRatio = 0.5f
        bottomSheetBehavior.peekHeight =
            Point().let { activity.windowManager.defaultDisplay.getSize(it); it.y } / 3
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    onBottomSheetSlide(slideOffset)
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

    fun setupErrorUi(retryBtnClickListener: OnClickListener) {
        errorBottomSheetBehavior = BottomSheetBehavior.from(errorCl)
        errorBottomSheetBehavior.isHideable = true
        errorBottomSheetBehavior.isFitToContents = true
        errorBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        errorRetryBtn.setOnClickListener(retryBtnClickListener)
    }

    fun setupLoadingUi() {
        loadingBottomSheetBehaviour = BottomSheetBehavior.from(loadingLl)
        loadingBottomSheetBehaviour.isHideable = true
        loadingBottomSheetBehaviour.isFitToContents = true
        loadingBottomSheetBehaviour.state = BottomSheetBehavior.STATE_HIDDEN
    }

    suspend fun showLoading(
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

    suspend fun hideLoading() = suspendCancellableCoroutine<Unit> {

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

    suspend fun showError(
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

    suspend fun hideError() = suspendCancellableCoroutine<Unit> {

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

    fun isItemListVisible(): Boolean {
        return bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN
    }

    fun updateItemList(
        activity: FragmentActivity,
        itemList: MutableList<RecyclerViewListItem>,
        itemTypeFactory: RecyclerViewTypeFactory,
    ) {
        val layoutState = itemsRv.layoutManager?.onSaveInstanceState()
        adapter =
            MultiItemAdapter(
                activity,
                itemTypeFactory,
                itemList
            )
        itemsRv.layoutManager?.onRestoreInstanceState(layoutState)
        itemsRv.adapter = adapter ?: return
    }

    suspend fun showItemList(
        activity: FragmentActivity,
        itemList: MutableList<RecyclerViewListItem>,
        itemTypeFactory: RecyclerViewTypeFactory,
    ) = suspendCancellableCoroutine<Unit> {

        adapter =
            MultiItemAdapter(
                activity,
                itemTypeFactory,
                itemList
            )

        itemsRv.adapter = adapter ?: return@suspendCancellableCoroutine

        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.isHideable = false
            it.resumeWith(Result.success(Unit))
            return@suspendCancellableCoroutine
        }

        bottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        bottomSheetBehavior.isHideable = false
                        bottomSheetBehavior.removeBottomSheetCallback(this)
                        it.resumeWith(Result.success(Unit))
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // do nothing
                }
            }
        )

        bottomSheet.post {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetBehavior.isHideable = false
        }
    }

    suspend fun hideItemList() = suspendCancellableCoroutine<Unit> {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            it.resumeWith(Result.success(Unit))
            return@suspendCancellableCoroutine
        }

        bottomSheetBehavior.addBottomSheetCallback(
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        bottomSheetBehavior.removeBottomSheetCallback(this)
                        it.resumeWith(Result.success(Unit))
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // do nothing
                }
            }
        )

        bottomSheet.post {
            bottomSheetBehavior.isHideable = true
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }
}