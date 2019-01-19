package com.edoctor.presentation.app.view

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.edoctor.R
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.data.injection.ChatModule
import com.edoctor.presentation.app.presenter.chat.ChatPresenter
import com.edoctor.presentation.app.presenter.chat.ChatPresenter.Event
import com.edoctor.presentation.app.presenter.chat.ChatPresenter.ViewState
import com.edoctor.presentation.architecture.fragment.BaseFragment
import javax.inject.Inject

class ChatFragment : BaseFragment<ChatPresenter, ViewState, Event>("ChatFragment") {

    @Inject
    override lateinit var presenter: ChatPresenter

    override val layoutRes: Int = R.layout.fragment_chat

    private lateinit var textView: TextView

    override fun init(applicationComponent: ApplicationComponent) {
        applicationComponent.plus(ChatModule()).inject(this)
        presenter.init()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.run {
            textView = findViewById(R.id.text_view)

            val editText = findViewById<EditText>(R.id.edit_text)
            val send = findViewById<Button>(R.id.send)
            val clean = findViewById<Button>(R.id.clean)

            send.setOnClickListener {
                textView.text = textView.text.toString() + "onMessageSent: ${editText.text}\n"
                presenter.sendMessage(editText.text.toString())
            }

            clean.setOnClickListener {
                textView.text = ""
            }
        }
    }

    override fun render(viewState: ViewState) {
        textView.text = viewState.messages.joinToString(separator = "\n")
    }

    override fun showEvent(event: Event) {
        if (event is Event.ShowChatError) {
            Toast.makeText(context, event.throwable.toString(), Toast.LENGTH_LONG).show()
        }
    }

}