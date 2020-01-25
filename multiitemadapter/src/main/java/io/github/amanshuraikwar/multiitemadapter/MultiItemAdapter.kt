@file:Suppress("unused")

package io.github.amanshuraikwar.multiitemadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

class MultiItemAdapter<T : RecyclerViewTypeFactory>(
    private val host: FragmentActivity,
    private val typeFactory: T,
    var items: MutableList<RecyclerViewListItem> = mutableListOf()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val contactView: View = LayoutInflater
            .from(host)
            .inflate(
                typeFactory.getLayout(viewType),
                parent,
                false
            )

        return typeFactory.createViewHolder(contactView, viewType) ?: throw RuntimeException()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        items[position].bind(holder.itemView, host)
    }

    override fun getItemViewType(position: Int): Int {
        return typeFactory.type(items[position])
    }

    fun addAll(newItems: List<RecyclerViewListItem>) {
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun remove(filter: (RecyclerViewListItem) -> Boolean): Boolean {
        val itemToBeRemoved = items.indexOfFirst(filter)
        if (itemToBeRemoved != -1) {
            items.removeAt(itemToBeRemoved)
            notifyItemRemoved(itemToBeRemoved)
        }
        return itemToBeRemoved != -1
    }

    fun clear() {
        items = mutableListOf()
        notifyDataSetChanged()
    }

    fun getItemAt(index: Int) = items[index]
}