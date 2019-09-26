package com.fabiosanto.infinite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.message_item.view.*
import java.lang.UnsupportedOperationException

class MessagesAdapter(private val onEndReached: () -> Unit) :
    ListAdapter<Item, MessagesAdapter.ItemVH>(DIFF) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Item.LoadingFooter -> R.layout.footer_item//add layouts
            is Item.Message -> R.layout.message_item//add layouts
            is Item.LoadingMessage -> R.layout.loading_message_item//add layouts
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVH {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.message_item -> MessageVH(view)
            R.layout.footer_item -> LoadingFooterVH(onEndReached, view)
            R.layout.loading_message_item -> LoadingMessageVH(view)
            else -> throw UnsupportedOperationException()//improve?
        }
    }

    override fun onBindViewHolder(holder: ItemVH, position: Int) {
        holder.onBind()
    }

    abstract class ItemVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun onBind()
    }

    class MessageVH(itemView: View) : ItemVH(itemView) {
        override fun onBind() {
            itemView.textView.text = "message $adapterPosition"
        }
    }

    class LoadingMessageVH(itemView: View) : ItemVH(itemView) {
        override fun onBind() {
        }
    }

    class LoadingFooterVH(private val onEndReached: () -> Unit, itemView: View) : ItemVH(itemView) {
        override fun onBind() {
            onEndReached()
        }
    }

    object DIFF : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return false
        }
    }
}