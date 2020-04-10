package io.github.amanshuraikwar.nxtbuz.ui.list

import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.nxtbuz.data.model.Transaction
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.item_overview_transaction.view.*

@ListItem(layoutResId = R.layout.item_overview_transaction)
class OverviewTransactionItem(
    private val transaction: Transaction,
    private val onClick: (Transaction) -> Unit
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {

        view.view1.setBackgroundColor(
            transaction.category.color
        )
        view.txnTitleTv.text = transaction.title
        view.amountTv.text = "$${transaction.amount.amount}"
        view.parentCl.setOnClickListener { onClick(transaction) }
    }
}