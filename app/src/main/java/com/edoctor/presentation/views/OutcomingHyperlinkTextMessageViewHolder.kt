package com.edoctor.presentation.views

import android.text.Html
import android.text.Spannable
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
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

    private val messageText by itemView.lazyFind<TextView>(R.id.messageText)

    override fun onBind(data: UserMessage) {
        super.onBind(data)
        messageText.text = data.text.toHtmlWithoutUnderline()
        messageText.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun String.toHtmlWithoutUnderline(): Spanned {
        val spanned = Html.fromHtml(this)

        if (spanned is Spannable) {
            for (u in spanned.getSpans(0, spanned.length, URLSpan::class.java)) {
                spanned.setSpan(object : UnderlineSpan() {
                    override fun updateDrawState(tp: TextPaint) {
                        tp.isUnderlineText = false
                    }
                }, spanned.getSpanStart(u), spanned.getSpanEnd(u), 0)
            }
        }

        return spanned
    }

}