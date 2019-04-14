package com.edoctor.presentation.views

import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import com.edoctor.R
import com.edoctor.data.entity.presentation.UserMessage
import com.edoctor.utils.lazyFind
import com.stfalcon.chatkit.messages.MessageHolders

class OutcomingHyperlinkTextMessageViewHolder(
    itemView: View,
    payload: Any?
) : MessageHolders.OutcomingTextMessageViewHolder<UserMessage>(itemView, payload) {

    val messageText by itemView.lazyFind<TextView>(R.id.messageText)

    override fun onBind(data: UserMessage) {
        super.onBind(data)
        messageText.text = Html.fromHtml(data.text)
        messageText.movementMethod = LinkMovementMethod.getInstance()
    }

}