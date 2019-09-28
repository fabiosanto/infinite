package com.fabiosanto.infinite

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.message_item.view.*
import java.lang.UnsupportedOperationException

class MessagesAdapter(
    private val onEndReached: (String) -> Unit,
    private val onRetryClicked: (String) -> Unit,
    private val onItemDismissed: (Int) -> Unit
) :
    ListAdapter<Item, MessagesAdapter.ItemVH>(DIFF) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Item.LoadingFooter -> R.layout.loading_message_item
            is Item.Message -> R.layout.message_item
            is Item.LoadingErrorPage -> R.layout.loading_error_page
            is Item.LoadingErrorCard -> R.layout.loading_error_item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVH {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.message_item -> MessageVH(view)
            R.layout.loading_message_item -> LoadingFooterVH(onEndReached, view)
            R.layout.loading_error_item -> LoadingErrorVH(onRetryClicked, view)
            R.layout.loading_error_page -> LoadingErrorVH(onRetryClicked, view)
            else -> throw UnsupportedOperationException() //improve?
        }
    }

    override fun onBindViewHolder(holder: ItemVH, position: Int) {
        holder.onBind(getItem(position))
    }

    abstract class ItemVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun onBind(item: Item)
    }

    internal class MessageVH(itemView: View) : ItemVH(itemView) {
        override fun onBind(item: Item) {
            item as Item.Message
            itemView.textView.text = item.content
            itemView.author.text = item.authorName
            itemView.time.text = item.time

            Picasso.get().load(item.authorPhotoUrl) //is this efficient?
                .transform(CircleTransform())
                .placeholder(R.drawable.avatar_placeholder).into(itemView.imageView)
        }
    }

    internal class LoadingErrorVH(private val onRetryClicked: (String) -> Unit, itemView: View) :
        ItemVH(itemView) {
        override fun onBind(item: Item) {
            item as Item.LoadingErrorCard
            itemView.findViewById<Button>(R.id.retry)
                .setOnClickListener { onRetryClicked(item.pageToken) }
        }
    }

    internal class LoadingFooterVH(private val onEndReached: (String) -> Unit, itemView: View) :
        ItemVH(itemView) {
        override fun onBind(item: Item) {
            item as Item.LoadingFooter
            onEndReached(item.pageToken)
        }
    }

    object DIFF : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            if (oldItem is Item.Message && newItem is Item.Message)
                return oldItem.id == newItem.id

            return false
        }
    }

    val swipeCallback = object :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            onItemDismissed(viewHolder.adapterPosition)
        }

        override fun getSwipeDirs(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            if (viewHolder !is MessagesAdapter.MessageVH) return 0;
            return super.getSwipeDirs(recyclerView, viewHolder)
        }
    }
}