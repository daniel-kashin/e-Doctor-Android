package com.edoctor.presentation.app.chat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.edoctor.R
import com.edoctor.data.entity.remote.TextMessage
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.data.injection.ChatModule
import com.edoctor.presentation.app.chat.ChatPresenter.Event
import com.edoctor.presentation.app.chat.ChatPresenter.ViewState
import com.edoctor.presentation.architecture.activity.BaseActivity
import com.edoctor.utils.CheckedIntentBuilder
import com.edoctor.utils.MessagesAdapter
import com.edoctor.utils.SessionExceptionHelper.onSessionException
import com.edoctor.utils.lazyFind
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesList
import javax.inject.Inject

class ChatActivity : BaseActivity<ChatPresenter, ViewState, Event>("ChatActivity") {

    companion object {
        // TODO: replace with id
        private const val EXTRA_CURRENT_USER_EMAIL = "SENDER_EMAIL"
        private const val EXTRA_RECIPIENT_EMAIL = "RECIPIENT_EMAIL"
    }

    @Inject
    override lateinit var presenter: ChatPresenter

    override val layoutRes: Int = R.layout.activity_chat

    private val messageInput by lazyFind<MessageInput>(R.id.message_input)
    private val messagesList by lazyFind<MessagesList>(R.id.messages_list)

    private lateinit var messagesAdapter: MessagesAdapter<TextMessage>

    override fun init(applicationComponent: ApplicationComponent) {
        val recipientEmail = intent.getStringExtra(EXTRA_RECIPIENT_EMAIL)
        val currentUserEmail = intent.getStringExtra(EXTRA_CURRENT_USER_EMAIL)
        applicationComponent.plus(ChatModule(currentUserEmail, recipientEmail)).inject(this)
        presenter.init(currentUserEmail)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        messageInput.setInputListener { input ->
            presenter.sendMessage(input.toString())
        }

        messagesAdapter = MessagesAdapter(presenter.currentUserEmail)
        messagesList.setAdapter(messagesAdapter)
    }

    override fun render(viewState: ViewState) {
        messagesAdapter.setMessages(viewState.messages) {
            messagesList.layoutManager?.scrollToPosition(0)
        }
    }

    override fun showEvent(event: Event) {
        when (event) {
            is Event.ShowException -> {
                Toast.makeText(this, event.throwable.toString(), Toast.LENGTH_LONG).show()
            }
            is Event.ShowSessionException -> {
                onSessionException()
            }
        }
    }

    class IntentBuilder(fragment: Fragment) : CheckedIntentBuilder(fragment) {

        private var currentUserEmail: String? = null
        private var recipientEmail: String? = null

        fun recipientEmail(recipientEmail: String) = apply { this.recipientEmail = recipientEmail }
        fun currentUserEmail(currentUserEmail: String) = apply { this.currentUserEmail = currentUserEmail }

        override fun areParamsValid() = recipientEmail != null && currentUserEmail != null

        override fun get() = Intent(context, ChatActivity::class.java)
            .putExtra(EXTRA_RECIPIENT_EMAIL, recipientEmail)
            .putExtra(EXTRA_CURRENT_USER_EMAIL, currentUserEmail)

    }

}