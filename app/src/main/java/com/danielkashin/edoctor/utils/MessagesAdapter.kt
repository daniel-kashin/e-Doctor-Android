package com.danielkashin.edoctor.utils

import androidx.recyclerview.widget.DiffUtil
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessagesListAdapter

class MessagesAdapter<MESSAGE : IMessage>(
    senderId: String,
    holders: MessageHolders,
    imageLoader: ImageLoader
) : MessagesListAdapter<MESSAGE>(senderId, holders, imageLoader) {

    fun setMessages(messages: List<MESSAGE>, afterSettingListener: (() -> Unit)? = null) {
        val oldItems = items

        this.items = mutableListOf()

        generateDateHeaders(messages)

        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                oldItems[oldItemPosition].item == items[newItemPosition].item
            override fun getOldListSize(): Int = oldItems.size
            override fun getNewListSize(): Int = items.size
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = true
        }, true).dispatchUpdatesTo(this)

        afterSettingListener?.invoke()
    }

}