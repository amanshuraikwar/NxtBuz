package io.github.amanshuraikwar.howmuch.ui.list

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.model.Transaction
import io.github.amanshuraikwar.howmuch.ui.main.overview.Last7DaysData
import io.github.amanshuraikwar.howmuch.ui.main.overview.Trend
import io.github.amanshuraikwar.howmuch.ui.onboarding.signin.hide
import io.github.amanshuraikwar.howmuch.ui.onboarding.signin.show
import io.github.amanshuraikwar.howmuch.util.ColorUtil
import io.github.amanshuraikwar.howmuch.util.dpToPx
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.item_overview_last_7_days.view.*
import kotlinx.android.synthetic.main.item_overview_last_7_days.view.amountTv
import kotlinx.android.synthetic.main.item_overview_transaction.view.*

@ListItem(layoutResId = R.layout.item_overview_last_7_days)
class Last7DaysItem(
    private val last7DaysData: Last7DaysData
) : RecyclerViewListItem {

    @SuppressLint("SetTextI18n")
    override fun bind(view: View, activity: FragmentActivity) {

        view.distributionBar.distributionBarData = last7DaysData.distributionBarData

        view.amountTv.text = "$${last7DaysData.distributionBarData.maxValue}"

        if (last7DaysData.distributionBarData.portions.isEmpty()) {
            view.distributionBar.hide()
        } else {
            view.distributionBar.show()
        }

//        last7DaysData.recentTransactions.forEach { txn ->
//
//            view.transactionsLl.addView(
//                activity.layoutInflater.inflate(
//                    R.layout.item_overview_transaction,
//                    null
//                ).apply {
//                    this.view1.setBackgroundColor(
//                        txn.category.color
//                    )
//                    this.txnTitleTv.text = txn.title
//                    this.amountTv.text = "$${txn.amount.amount}"
//                    this.parentCl.setOnClickListener { onClick(txn) }
//                }
//            )
//
//            view.transactionsLl.addView(
//                activity.layoutInflater.inflate(R.layout.item_divider, null).apply {
//                    layoutParams = LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        activity.dpToPx(1f).toInt()
//                    )
//                }
//            )
//
//        }

//        view.transactionsLl.addView(
//            activity.layoutInflater.inflate(R.layout.item_text_button, null)
//        )

        view.trendIv.setImageResource(
            when (last7DaysData.trend) {
                Trend.UP -> R.drawable.ic_round_trending_up_24
                Trend.DOWN -> R.drawable.ic_round_trending_down_24
                Trend.FLAT -> R.drawable.ic_round_trending_flat_24
            }
        )

        view.trendIv.imageTintList = ColorStateList.valueOf(
            when (last7DaysData.trend) {
                Trend.UP -> ContextCompat.getColor(activity, R.color.green)
                Trend.DOWN -> ContextCompat.getColor(activity, R.color.red)
                Trend.FLAT -> ContextCompat.getColor(activity, R.color.color_primary)
            }
        )
    }
}