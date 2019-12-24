package io.github.amanshuraikwar.howmuch.ui.list

import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.model.Transaction
import io.github.amanshuraikwar.howmuch.ui.main.overview.BudgetAwareCategory
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.item_overview_transaction.view.*

@ListItem(layoutResId = R.layout.item_overview_transaction)
class OverviewBudgetCategoryItem(
    private val category: BudgetAwareCategory,
    private val onClick: (BudgetAwareCategory) -> Unit
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {

        view.view1.setBackgroundColor(
            category.category.color
        )
        view.txnTitleTv.text = category.category.name
        view.amountTv.text = "$${category.category.monthlyLimit.amount - category.amount.amount} Left"
        view.parentCl.setOnClickListener { onClick(category) }
    }
}