package io.github.amanshuraikwar.howmuch.ui.list

import android.view.View
import androidx.annotation.DrawableRes
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.item_info_small.view.*

@ListItem(layoutResId = R.layout.item_info_small)
class InfoItem(
    private val info: String,
    @DrawableRes private val iconResId: Int = R.drawable.ic_round_info_24
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {
        view.stopNameTv.text = info
        view.iconIv.setImageResource(iconResId)
    }
}