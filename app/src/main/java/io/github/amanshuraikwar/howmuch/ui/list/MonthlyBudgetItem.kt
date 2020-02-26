package io.github.amanshuraikwar.howmuch.ui.list

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem

@ListItem(layoutResId = R.layout.item_overview_monthly_budget)
class MonthlyBudgetItem(
) : RecyclerViewListItem {

    @SuppressLint("SetTextI18n")
    override fun bind(view: View, activity: FragmentActivity) {
    }
}