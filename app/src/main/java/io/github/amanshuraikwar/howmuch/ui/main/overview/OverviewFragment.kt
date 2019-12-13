package io.github.amanshuraikwar.howmuch.ui.main.overview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerFragment
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.model.Transaction
import io.github.amanshuraikwar.howmuch.domain.result.EventObserver
import io.github.amanshuraikwar.howmuch.ui.list.Last7DaysItem
import io.github.amanshuraikwar.howmuch.ui.list.MonthlyBudgetItem
import io.github.amanshuraikwar.howmuch.ui.list.RecyclerViewTypeFactoryGenerated
import io.github.amanshuraikwar.howmuch.util.ModelUtil
import io.github.amanshuraikwar.howmuch.util.dpToPx
import io.github.amanshuraikwar.howmuch.util.toDisplayDate
import io.github.amanshuraikwar.howmuch.util.viewModelProvider
import io.github.amanshuraikwar.multiitemadapter.MultiItemAdapter
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.android.synthetic.main.item_overview_last_7_days.*
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

        Log.d(TAG, "setupViewModel: UI is in thread ${Thread.currentThread().name}")

        requireActivity().let { activity ->

            viewModel = viewModelProvider(viewModelFactory) {
                colorControlNormalResId = TypedValue().let {
                    activity.theme.resolveAttribute(R.attr.colorControlNormal, it, true)
                    ContextCompat.getColor(activity, it.resourceId)
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

            viewModel.overviewData.observe(
                this,
                Observer {

                    val listItems = mutableListOf<RecyclerViewListItem>()
                    listItems.add(Last7DaysItem(it.last7DaysData, {}))
                    listItems.add(MonthlyBudgetItem(it.monthlyBudgetData, {}))

                    val adapter = MultiItemAdapter(activity, RecyclerViewTypeFactoryGenerated(), listItems)
                    itemsRv.layoutManager = LinearLayoutManager(activity)
                    itemsRv.adapter = adapter
                }
            )
        }
    }
}