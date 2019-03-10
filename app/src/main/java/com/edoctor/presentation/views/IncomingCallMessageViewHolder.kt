package com.edoctor.presentation.views

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.edoctor.R
import com.edoctor.data.entity.presentation.CallStatusMessage
import com.edoctor.utils.lazyFind
import com.edoctor.utils.setDrawableWithTint
import com.stfalcon.chatkit.messages.MessageHolders

class IncomingCallMessageViewHolder(
    itemView: View,
    payload: Any?
) : MessageHolders.IncomingTextMessageViewHolder<CallStatusMessage>(itemView, payload) {

    val imageArrow by itemView.lazyFind<ImageView>(R.id.image_arrow)
    val messageTime by itemView.lazyFind<TextView>(R.id.messageTime)
    val imagePhone by itemView.lazyFind<ImageView>(R.id.image_phone)

    override fun onBind(data: CallStatusMessage) {
        super.onBind(data)
        if (data.callStatus == CallStatusMessage.CallStatus.CANCELLED) {
            imageArrow.setDrawableWithTint(R.drawable.ic_incoming_call, R.color.decline_call)
            imagePhone.setDrawableWithTint(R.drawable.ic_decline_call, R.color.decline_call)
        } else {
            imageArrow.setDrawableWithTint(R.drawable.ic_incoming_call, R.color.accept_call)
            imagePhone.setDrawableWithTint(R.drawable.ic_accept_call, R.color.accept_call)
        }
    }

}