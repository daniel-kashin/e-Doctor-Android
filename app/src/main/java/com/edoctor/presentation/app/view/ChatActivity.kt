package com.edoctor.presentation.app.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.edoctor.R
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.data.injection.ChatModule
import com.edoctor.presentation.app.presenter.chat.ChatPresenter
import com.edoctor.presentation.app.presenter.chat.ChatPresenter.Event
import com.edoctor.presentation.app.presenter.chat.ChatPresenter.ViewState
import com.edoctor.presentation.architecture.activity.BaseActivity
import com.edoctor.utils.CheckedIntentBuilder
import com.edoctor.utils.lazyFind
import javax.inject.Inject

class ChatActivity : BaseActivity<ChatPresenter, ViewState, Event>("ChatFragment") {

    companion object {
        private val EXTRA_RECIPIENT_EMAIL = "RECIPIENT_EMAIL"
    }

    @Inject
    override lateinit var presenter: ChatPresenter

    override val layoutRes: Int = R.layout.activity_chat

    private val textView by lazyFind<TextView>(R.id.text_view)
    private val editText by lazyFind<EditText>(R.id.edit_text)
    private val send by lazyFind<Button>(R.id.send)
    private val clean by lazyFind<Button>(R.id.clean)


    override fun init(applicationComponent: ApplicationComponent) {
        val recipientEmail = intent.getStringExtra(EXTRA_RECIPIENT_EMAIL)
        applicationComponent.plus(ChatModule(recipientEmail)).inject(this)
        presenter.init()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        send.setOnClickListener {
            textView.text = textView.text.toString() + "onMessageSent: ${editText.text}\n"
            presenter.sendMessage(editText.text.toString())
        }

        clean.setOnClickListener {
            textView.text = ""
        }

        lifecycle
    }

    override fun render(viewState: ViewState) {
        textView.text = viewState.messages.joinToString(separator = "\n")
    }

    override fun showEvent(event: Event) {
        if (event is Event.ShowChatError) {
            Toast.makeText(this, event.throwable.toString(), Toast.LENGTH_LONG).show()
        }
    }

    class IntentBuilder : CheckedIntentBuilder {

        constructor(fragment: Fragment) : super(fragment)
        constructor(context: Context) : super(context)

        private var recipientEmail: String? = null

        fun recipientEmail(recipientEmail: String) = apply { this.recipientEmail = recipientEmail }

        override fun areParamsValid() = recipientEmail != null

        override fun get() = Intent(context, ChatActivity::class.java)
            .putExtra(EXTRA_RECIPIENT_EMAIL, recipientEmail)

    }

}