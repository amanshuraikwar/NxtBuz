package io.github.amanshuraikwar.nxtbuz.listitem

import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.item_setting_heading.view.*

@ListItem(layoutResName = "item_setting_heading")
class SettingsHeadingItem(
    private val heading: String
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {
        view.headerTv.text = heading
    }
}