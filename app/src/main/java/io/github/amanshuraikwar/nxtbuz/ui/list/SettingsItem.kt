package io.github.amanshuraikwar.nxtbuz.ui.list

import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.nxtbuz.R
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.item_setting.view.*

@ListItem(layoutResId = R.layout.item_setting)
class SettingsItem(
    private val title: String,
    private val description: String,
    private val value: String
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {
        view.titleTv.text = title
        view.descriptionTv.text = description
        view.valueTv.text = value
    }
}