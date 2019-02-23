package com.edoctor.utils

import androidx.recyclerview.widget.DiffUtil
import com.stfalcon.chatkit.commons.models.IDialog
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.dialogs.DialogsListAdapter

class DialogsAdapter<DIALOG : IDialog<out IMessage>> : DialogsListAdapter<DIALOG>(null) {

    fun setDialogs(dialogs: List<DIALOG>, afterSettingListener: (() -> Unit)? = null) {
        val oldItems = items

        this.items = mutableListOf()

        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                oldItems[oldItemPosition] == items[newItemPosition]
            override fun getOldListSize(): Int = oldItems.size
            override fun getNewListSize(): Int = items.size
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = true
        }, true).dispatchUpdatesTo(this)

        afterSettingListener?.invoke()
    }

}