package com.danielkashin.edoctor.presentation.views

import android.text.Html
import android.text.Spannable
import android.text.Spanned
import android.view.View
import com.danielkashin.edoctor.R
import com.danielkashin.edoctor.data.entity.presentation.UserMessage
import com.danielkashin.edoctor.utils.lazyFind
import com.stfalcon.chatkit.messages.MessageHolders
import android.text.method.LinkMovementMethod
import android.widget.TextView
import android.text.TextPaint
import android.text.style.UnderlineSpan
import android.text.style.URLSpan


class IncomingHyperlinkTextMessageViewHolder(
    itemView: View,
    payload: Any?
) : MessageHolders.IncomingTextMessageViewHolder<UserMessage>(itemView, payload) {

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