package io.github.amanshuraikwar.howmuch.ui.main.overview

import android.annotation.SuppressLint
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
import io.github.amanshuraikwar.howmuch.data.model.Transaction
import io.github.amanshuraikwar.howmuch.domain.result.EventObserver
import io.github.amanshuraikwar.howmuch.ui.list.*
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
                    //activity.theme.resolveAttribute(R.attr.colorControlHighlight, it, true)
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

            viewModel.overviewData.observe(
                this,
                Observer {

                    val listItems = mutableListOf<RecyclerViewListItem>()

                    it.alert?.run {
                        listItems.add(
                            HeaderItem("Alert")
                        )
                        listItems.add(
                            InfoItem(this.msg, R.drawable.ic_round_access_time_24)
                        )
                    }

                    listItems.add(
                        HeaderItem("Last 7 Days")
                    )

                    listItems.add(Last7DaysItem(it.last7DaysData))

                    it.last7DaysData.recentTransactions.forEach {
                        listItems.add(
                            OverviewTransactionItem(
                                it,
                                {}
                            )
                        )
                    }

                    listItems.add(
                        OverviewButtonItem(
                            if (it.last7DaysData.recentTransactions.isEmpty())
                                "See Older Transactions"
                            else
                                "See All",
                            {}
                        )
                    )

                    listItems.add(
                        HeaderItem("Monthly Budget")
                    )

                    listItems.add(MonthlyBudgetItem(it.monthlyBudgetData))

                    it.monthlyBudgetData.minBudgetRemainingCategories.forEach {
                        listItems.add(
                            OverviewBudgetCategoryItem(
                                it,
                                {}
                            )
                        )
                    }

                    listItems.add(
                        OverviewButtonItem("See Monthly Budget", {})
                    )

                    val adapter =
                        MultiItemAdapter(activity, RecyclerViewTypeFactoryGenerated(), listItems)
                    itemsRv.layoutManager = LinearLayoutManager(activity)
                    itemsRv.adapter = adapter
                }
            )
        }
    }
}