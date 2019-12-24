package io.github.amanshuraikwar.howmuch.ui.list

import android.annotation.SuppressLint
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.model.Category
import io.github.amanshuraikwar.howmuch.ui.main.overview.BudgetStatus
import io.github.amanshuraikwar.howmuch.ui.main.overview.MonthlyBudgetData
import io.github.amanshuraikwar.howmuch.ui.onboarding.signin.hide
import io.github.amanshuraikwar.howmuch.ui.onboarding.signin.show
import io.github.amanshuraikwar.howmuch.util.ColorUtil
import io.github.amanshuraikwar.howmuch.util.dpToPx
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.item_overview_last_7_days.view.*
import kotlinx.android.synthetic.main.item_overview_last_7_days.view.amountTv
import kotlinx.android.synthetic.main.item_overview_last_7_days.view.distributionBar
import kotlinx.android.synthetic.main.item_overview_monthly_budget.view.*

@ListItem(layoutResId = R.layout.item_overview_monthly_budget)
class MonthlyBudgetItem(
    private val monthlyBudgetData: MonthlyBudgetData
) : RecyclerViewListItem {

    @SuppressLint("SetTextI18n")
    override fun bind(view: View, activity: FragmentActivity) {
        view.distributionBar.distributionBarData = monthlyBudgetData.distributionBarData
        view.amountTv.text = "$${monthlyBudgetData.budgetDifference.amount}"
        view.leftTv.text =
            if (monthlyBudgetData.budgetStatus == BudgetStatus.IN_BUDGET)
                "Left"
            else
                "Over"
        view.leftTv.setTextColor(
            activity.getColor(
                if (monthlyBudgetData.budgetStatus == BudgetStatus.IN_BUDGET)
                    R.color.green
                else
                    R.color.red
            )
        )
    }
}