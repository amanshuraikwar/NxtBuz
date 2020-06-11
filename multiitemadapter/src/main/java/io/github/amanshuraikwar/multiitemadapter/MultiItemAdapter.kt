@file:Suppress("unused")

package io.github.amanshuraikwar.multiitemadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import java.lang.IllegalArgumentException

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

    fun prepareRemove(filter: (RecyclerViewListItem) -> Boolean): RemoveEvent? {
        val itemToBeRemoved = items.indexOfFirst(filter)
        if (itemToBeRemoved != -1) return RemoveEvent(itemToBeRemoved)
        return null
    }

    fun clear() {
        items = mutableListOf()
        notifyDataSetChanged()
    }

    fun getItemAt(index: Int) = items[index]

    inner class RemoveEvent constructor(private val currentIndex: Int) {

        var secondIndex: Int = -1

        init {
            if (currentIndex < 0 || currentIndex >= items.size) {
                throw IllegalArgumentException("Index $currentIndex out of bounds.")
            }
        }

        fun alsoRemove(predicate: RemoveEvent.(currentIndex: Int) -> Int): RemoveEvent {
            val secondIndex = predicate(currentIndex)
            if (secondIndex >= 0 && secondIndex < items.size) {
                this.secondIndex = secondIndex
            }
            return this
        }

        fun itemAt(index: Int): RecyclerViewListItem? {
            if (index < 0) return null
            if (index >= items.size) return null
            return items[index]
        }

        fun nextItem(): RecyclerViewListItem? {
            if (currentIndex + 1 >= items.size) return null
            if (currentIndex + 1 < 0) return null
            return items[currentIndex + 1]
        }

        fun previousItem(): RecyclerViewListItem? {
            if (currentIndex - 1 >= items.size) return null
            if (currentIndex - 1 < 0) return null
            return items[currentIndex - 1]
        }

        fun doItNow() {
            if (currentIndex == -1) return
            items.removeAt(currentIndex)
            notifyItemRemoved(currentIndex)
            if (secondIndex == -1) return
            if (secondIndex > currentIndex) {
                secondIndex--
            }
            items.removeAt(secondIndex)
            notifyItemRemoved(secondIndex)
        }

        fun isCurrentItemLast(): Boolean {
            return currentIndex == items.size - 1
        }
    }
}