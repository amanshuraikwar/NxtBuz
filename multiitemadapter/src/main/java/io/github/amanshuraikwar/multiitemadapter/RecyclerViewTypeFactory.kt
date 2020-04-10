package io.github.amanshuraikwar.multiitemadapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

interface RecyclerViewTypeFactory {
    fun getLayout(viewType: Int): Int
    fun createViewHolder(parent: View, viewType: Int): RecyclerView.ViewHolder?
    fun type(listItem: RecyclerViewListItem): Int
}