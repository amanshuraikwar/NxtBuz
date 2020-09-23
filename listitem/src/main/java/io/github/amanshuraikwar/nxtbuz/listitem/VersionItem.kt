package io.github.amanshuraikwar.nxtbuz.listitem

import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.item_version.view.*

@ListItem(layoutResName = "item_version")
class VersionItem(
    private val version: String
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {
        view.versionTv.text = version
    }
}