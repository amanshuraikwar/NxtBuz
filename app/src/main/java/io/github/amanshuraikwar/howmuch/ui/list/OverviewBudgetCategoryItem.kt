package io.github.amanshuraikwar.howmuch.ui.list

import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem

@ListItem(layoutResId = R.layout.item_overview_transaction)
class OverviewBudgetCategoryItem(
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {
    }
}