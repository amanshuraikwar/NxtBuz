package io.github.amanshuraikwar.nxtbuz.ui.list

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem

@ListItem(layoutResId = R.layout.item_overview_last_7_days)
class Last7DaysItem(
) : RecyclerViewListItem {

    @SuppressLint("SetTextI18n")
    override fun bind(view: View, activity: FragmentActivity) {



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

    }
}