package com.edoctor.utils

import androidx.recyclerview.widget.DiffUtil
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.messages.MessagesListAdapter

class MessagesAdapter<MESSAGE : IMessage>(senderId: String) : MessagesListAdapter<MESSAGE>(senderId, null) {

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

//    protected fun generateMessagesWithDates(messages: List<MESSAGE>) {
//        for (i in messages.indices) {
//            val message = messages[i]
//            this.items.add(Wrapper(message))
//            if (messages.size > i + 1) {
//                val nextMessage = messages[i + 1]
//                if (!DateFormatter.isSameDay(message.createdAt, nextMessage.createdAt)) {
//                    this.items.add(Wrapper(message.createdAt))
//                }
//            } else {
//                this.items.add(Wrapper(message.createdAt))
//            }
//        }
//    }

}