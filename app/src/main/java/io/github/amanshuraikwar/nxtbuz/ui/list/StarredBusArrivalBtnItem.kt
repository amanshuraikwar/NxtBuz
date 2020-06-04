package io.github.amanshuraikwar.nxtbuz.ui.list

import android.view.View
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.R
import kotlinx.android.synthetic.main.item_starred_bus_arrival_btn.view.*

@ListItem(layoutResId = R.layout.item_starred_bus_arrival_btn)
class StarredBusArrivalBtnItem(
    private val txt: String,
    private val onClicked: () -> Unit
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {
        view.parentCv.setOnClickListener {
            onClicked()
        }
        view.txtTv.text = txt
    }
}