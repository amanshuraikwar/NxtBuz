package io.github.amanshuraikwar.howmuch.ui.list

import android.content.res.ColorStateList
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.model.Transaction
import io.github.amanshuraikwar.howmuch.ui.main.overview.Last7DaysData
import io.github.amanshuraikwar.howmuch.ui.main.overview.Trend
import io.github.amanshuraikwar.howmuch.util.ColorUtil
import io.github.amanshuraikwar.howmuch.util.dpToPx
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.item_info_small.view.*
import kotlinx.android.synthetic.main.item_overview_last_7_days.view.*
import kotlinx.android.synthetic.main.item_overview_last_7_days.view.amountTv
import kotlinx.android.synthetic.main.item_overview_transaction.view.*
import kotlinx.android.synthetic.main.item_overview_transaction.view.parentCl

@ListItem(layoutResId = R.layout.item_info_small)
class InfoItem(
    private val info: String,
    @DrawableRes private val iconResId: Int = R.drawable.ic_round_info_24
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {
        view.infoTv.text = info
        view.iconIv.setImageResource(iconResId)
    }
}