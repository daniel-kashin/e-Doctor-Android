package com.infostrategic.edoctor.utils

import androidx.recyclerview.widget.DiffUtil
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.commons.models.IDialog
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.dialogs.DialogsListAdapter

class DialogsAdapter<DIALOG : IDialog<out IMessage>>(
    imageLoader: ImageLoader
) : DialogsListAdapter<DIALOG>(imageLoader) {

    fun setDialogs(dialogs: List<DIALOG>, afterSettingListener: (() -> Unit)? = null) {
        val oldItems = items

        this.items = dialogs

        DiffUtil
            .calculateDiff(
                object : DiffUtil.Callback() {
                    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                        oldItems[oldItemPosition] == items[newItemPosition]

                    override fun getOldListSize(): Int = oldItems.size
                    override fun getNewListSize(): Int = items.size
                    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = true
                },
                true
            )
            .dispatchUpdatesTo(this)

        afterSettingListener?.invoke()
    }

}