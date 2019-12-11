package io.github.amanshuraikwar.howmuch.ui.main.overview

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.android.support.DaggerFragment
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.model.Transaction
import io.github.amanshuraikwar.howmuch.domain.result.EventObserver
import io.github.amanshuraikwar.howmuch.util.ModelUtil
import io.github.amanshuraikwar.howmuch.util.dpToPx
import io.github.amanshuraikwar.howmuch.util.toDisplayDate
import io.github.amanshuraikwar.howmuch.util.viewModelProvider
import kotlinx.android.synthetic.main.item_overview_last_7_days.*
import javax.inject.Inject

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

            viewModel = viewModelProvider(viewModelFactory)

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

            viewModel.last7Days.observe(
                this,
                Observer {

                    divider1.distributionBarData = it.distributionBarData

                    amountTv.text = "$${it.distributionBarData.maxValue}"

                    it.recentTransactions.forEach { txn ->

                        transactionsLl.addView(
                            layoutInflater.inflate(
                                R.layout.item_overview_transaction,
                                null
                            ).apply {
                                findViewById<View>(R.id.view1).setBackgroundColor(
                                    ContextCompat.getColor(
                                        activity, ModelUtil.getCategoryColor(txn.category.name)
                                    )
                                )
                                findViewById<TextView>(R.id.txnTitleTv).text = txn.title
                                findViewById<TextView>(R.id.amountTv).text = "$${txn.amount.amount}"
                                findViewById<View>(R.id.parentCl).setOnClickListener { onTransactionClicked(txn) }
                            }
                        )

                        transactionsLl.addView(
                            layoutInflater.inflate(R.layout.item_divider, null).apply {
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    activity.dpToPx(1f).toInt()
                                )
                            }
                        )

                    }

                    transactionsLl.addView(
                        layoutInflater.inflate(R.layout.item_text_button, null)
                    )

                    trendIv.setImageResource(
                        when (it.trend) {
                            Trend.UP -> R.drawable.ic_round_trending_up_24
                            Trend.DOWN -> R.drawable.ic_round_trending_down_24
                            Trend.FLAT -> R.drawable.ic_round_trending_flat_24
                        }
                    )

                    trendIv.imageTintList = ColorStateList.valueOf(
                        when (it.trend) {
                            Trend.UP -> ContextCompat.getColor(activity, R.color.green)
                            Trend.DOWN -> ContextCompat.getColor(activity, R.color.red)
                            Trend.FLAT -> ContextCompat.getColor(activity, R.color.color_primary)
                        }
                    )
                }
            )
        }
    }
}