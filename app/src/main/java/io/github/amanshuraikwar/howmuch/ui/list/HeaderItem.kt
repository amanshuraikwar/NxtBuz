package io.github.amanshuraikwar.howmuch.ui.list

import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.item_overview_header.view.*

@ListItem(layoutResId = R.layout.item_overview_header)
class HeaderItem(
    private val header: String
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {
        view.headerTv.text = header
    }
}