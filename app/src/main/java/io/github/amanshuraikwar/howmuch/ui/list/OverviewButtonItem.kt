package io.github.amanshuraikwar.howmuch.ui.list

import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import io.github.amanshuraikwar.annotations.ListItem
import io.github.amanshuraikwar.howmuch.R
import io.github.amanshuraikwar.howmuch.data.model.Transaction
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import kotlinx.android.synthetic.main.item_overview_transaction.view.*
import kotlinx.android.synthetic.main.item_text_button.view.*

@ListItem(layoutResId = R.layout.item_text_button)
class OverviewButtonItem(
    private val msg: String,
    private val onClick: (Transaction) -> Unit
) : RecyclerViewListItem {

    override fun bind(view: View, activity: FragmentActivity) {
        view.btn.text = msg
    }
}