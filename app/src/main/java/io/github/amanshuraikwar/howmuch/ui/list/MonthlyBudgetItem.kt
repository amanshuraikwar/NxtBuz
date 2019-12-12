package io.github.amanshuraikwar.howmuch.ui.list

import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.model.Category
import io.github.amanshuraikwar.howmuch.ui.main.overview.MonthlyBudgetData
import io.github.amanshuraikwar.howmuch.util.ModelUtil
import io.github.amanshuraikwar.howmuch.util.dpToPx
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.item_overview_last_7_days.view.*
import kotlinx.android.synthetic.main.item_overview_last_7_days.view.amountTv
import kotlinx.android.synthetic.main.item_overview_transaction.view.*

@ListItem(layoutResId = R.layout.item_overview_last_7_days)
class MonthlyBudgetItem(
    private val monthlyBudgetData: MonthlyBudgetData,
    private val onClick: (Category) -> Unit
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {

        view.divider1.distributionBarData = monthlyBudgetData.distributionBarData

        view.amountTv.text = "$${monthlyBudgetData.distributionBarData.maxValue}"

        monthlyBudgetData.minBudgetRemainingCategories.forEach { txn ->

            view.transactionsLl.addView(
                activity.layoutInflater.inflate(
                    R.layout.item_overview_transaction,
                    null
                ).apply {
                    this.view1.setBackgroundColor(
                        ContextCompat.getColor(
                            activity, ModelUtil.getCategoryColor(txn.category.name)
                        )
                    )
                    this.txnTitleTv.text = txn.category.name
                    this.amountTv.text = "$${txn.amount.amount}"
                    this.parentCl.setOnClickListener { onClick(txn.category) }
                }
            )

            view.transactionsLl.addView(
                activity.layoutInflater.inflate(R.layout.item_divider, null).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        activity.dpToPx(1f).toInt()
                    )
                }
            )

        }

        view.transactionsLl.addView(
            activity.layoutInflater.inflate(R.layout.item_text_button, null)
        )

        /*
        view.trendIv.setImageResource(
            when (monthlyBudgetData.trend) {
                Trend.UP -> R.drawable.ic_round_trending_up_24
                Trend.DOWN -> R.drawable.ic_round_trending_down_24
                Trend.FLAT -> R.drawable.ic_round_trending_flat_24
            }
        )

        view.trendIv.imageTintList = ColorStateList.valueOf(
            when (monthlyBudgetData.trend) {
                Trend.UP -> ContextCompat.getColor(activity, R.color.green)
                Trend.DOWN -> ContextCompat.getColor(activity, R.color.red)
                Trend.FLAT -> ContextCompat.getColor(activity, R.color.color_primary)
            }
        )
         */
    }
}