package io.github.amanshuraikwar.nxtbuz.ui.list

import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.R
import kotlinx.android.synthetic.main.item_setting_heading.view.*

@ListItem(layoutResId = R.layout.item_setting_heading)
class SettingsHeadingItem(
    private val heading: String
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {
        view.headerTv.text = heading
    }
}