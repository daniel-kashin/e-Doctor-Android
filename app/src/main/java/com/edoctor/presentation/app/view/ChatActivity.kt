package com.edoctor.presentation.app.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.edoctor.R
import com.edoctor.data.entity.remote.TextMessage
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.data.injection.ChatModule
import com.edoctor.presentation.app.presenter.chat.ChatPresenter
import com.edoctor.presentation.app.presenter.chat.ChatPresenter.Event
import com.edoctor.presentation.app.presenter.chat.ChatPresenter.ViewState
import com.edoctor.presentation.architecture.activity.BaseActivity
import com.edoctor.utils.CheckedIntentBuilder
import com.edoctor.utils.MessagesAdapter
import com.edoctor.utils.lazyFind
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesList
import javax.inject.Inject

class ChatActivity : BaseActivity<ChatPresenter, ViewState, Event>("ChatFragment") {

    companion object {
        // TODO: replace with id
        private val EXTRA_SENDER_EMAIL = "SENDER_EMAIL"
        private val EXTRA_RECIPIENT_EMAIL = "RECIPIENT_EMAIL"
    }

    @Inject
    override lateinit var presenter: ChatPresenter

    override val layoutRes: Int = R.layout.activity_chat

    private val messageInput by lazyFind<MessageInput>(R.id.message_input)
    private val messagesList by lazyFind<MessagesList>(R.id.messages_list)

    private lateinit var messagesAdapter: MessagesAdapter<TextMessage>

    override fun init(applicationComponent: ApplicationComponent) {
        val recipientEmail = intent.getStringExtra(EXTRA_RECIPIENT_EMAIL)
        val senderEmail = intent.getStringExtra(EXTRA_SENDER_EMAIL)
        applicationComponent.plus(ChatModule(senderEmail, recipientEmail)).inject(this)
        presenter.init(senderEmail)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        messageInput.setInputListener { input ->
            presenter.sendMessage(input.toString())
            true
        }

        messagesAdapter = MessagesAdapter(presenter.senderEmail)
        messagesList.setAdapter(messagesAdapter)
    }

    override fun render(viewState: ViewState) {
        messagesAdapter.setMessages(viewState.messages) {
            messagesList.layoutManager?.scrollToPosition(0)
        }
    }

    override fun showEvent(event: Event) {
        if (event is Event.ShowChatError) {
            Toast.makeText(this, event.throwable.toString(), Toast.LENGTH_LONG).show()
        }
    }

    class IntentBuilder : CheckedIntentBuilder {

        constructor(fragment: Fragment) : super(fragment)
        constructor(context: Context) : super(context)

        private var senderEmail: String? = null
        private var recipientEmail: String? = null

        fun recipientEmail(recipientEmail: String) = apply { this.recipientEmail = recipientEmail }
        fun senderEmail(senderEmail: String) = apply { this.senderEmail = senderEmail }

        override fun areParamsValid() = recipientEmail != null && senderEmail != null

        override fun get() = Intent(context, ChatActivity::class.java)
            .putExtra(EXTRA_RECIPIENT_EMAIL, recipientEmail)
            .putExtra(EXTRA_SENDER_EMAIL, senderEmail)

    }

}