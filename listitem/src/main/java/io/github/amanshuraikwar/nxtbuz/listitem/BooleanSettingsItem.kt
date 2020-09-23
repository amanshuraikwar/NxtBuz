package io.github.amanshuraikwar.nxtbuz.listitem

import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.item_boolean_setting.view.*

@ListItem(layoutResName = "item_boolean_setting")
class BooleanSettingsItem(
    private val title: String,
    private val description: (value: Boolean) -> String,
    private var currentVal: Boolean,
    private val onClick: (newValue: Boolean) -> Unit,
    private val last: Boolean = false
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {
        view.parentCl.setOnClickListener { view.switchSm.toggle() }
        view.switchSm.setOnCheckedChangeListener { _, isChecked ->
            onClick(isChecked)
            currentVal = isChecked
            view.descriptionTv.text = description(currentVal)
        }
        view.titleTv.text = title
        view.descriptionTv.text = description(currentVal)
        view.switchSm.isChecked = currentVal
        view.divider.visibility = if (last) View.INVISIBLE else View.VISIBLE
    }
}