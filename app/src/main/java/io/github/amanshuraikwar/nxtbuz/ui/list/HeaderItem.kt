package io.github.amanshuraikwar.nxtbuz.ui.list

import android.view.View
import androidx.annotation.DrawableRes
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.item_overview_header.view.*

@ListItem(layoutResId = R.layout.item_overview_header)
class HeaderItem(
    private val header: String,
    @DrawableRes var icon: Int? = null,
    private val onClickListener: () -> Unit = {}
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {
        view.headerTv.text = header
        view.headerTv.setOnClickListener { onClickListener() }
        view.headerTv.setCompoundDrawablesWithIntrinsicBounds(
            0, 0, icon ?: return, 0
        )
    }
}