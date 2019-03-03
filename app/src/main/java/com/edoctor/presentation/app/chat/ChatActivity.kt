package com.edoctor.presentation.app.chat

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.edoctor.R
import com.edoctor.data.entity.presentation.Message
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.data.injection.ChatModule
import com.edoctor.presentation.app.chat.ChatPresenter.Event
import com.edoctor.presentation.app.chat.ChatPresenter.ViewState
import com.edoctor.presentation.architecture.activity.BaseActivity
import com.edoctor.utils.*
import com.edoctor.utils.SessionExceptionHelper.onSessionException
import com.facebook.react.modules.core.PermissionListener
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesList
import org.jitsi.meet.sdk.JitsiMeetActivityInterface
import org.jitsi.meet.sdk.JitsiMeetView
import org.jitsi.meet.sdk.JitsiMeetViewAdapter
import org.jitsi.meet.sdk.ReactActivityLifecycleCallbacks
import javax.inject.Inject

class ChatActivity : BaseActivity<ChatPresenter, ViewState, Event>("ChatActivity"), JitsiMeetActivityInterface {

    companion object {
        // TODO: replace with id
        private const val EXTRA_CURRENT_USER_EMAIL = "SENDER_EMAIL"
        private const val EXTRA_RECIPIENT_EMAIL = "RECIPIENT_EMAIL"
    }

    @Inject
    override lateinit var presenter: ChatPresenter

    override val layoutRes: Int = R.layout.activity_chat

    private val activityRoot by lazyFind<FrameLayout>(R.id.activity_root)
    private val toolbar by lazyFind<Toolbar>(R.id.toolbar)
    private val toolbarPrimaryText by lazyFind<TextView>(R.id.toolbar_primary_text)
    private val toolbarSecondaryText by lazyFind<TextView>(R.id.toolbar_secondary_text)
    private val iconCall by lazyFind<ImageView>(R.id.icon_call)
    private val messageInput by lazyFind<MessageInput>(R.id.message_input)
    private val messagesList by lazyFind<MessagesList>(R.id.messages_list)
    private lateinit var jitsiMeetView: JitsiMeetView

    private lateinit var messagesAdapter: MessagesAdapter<Message>

    override fun init(applicationComponent: ApplicationComponent) {
        val recipientEmail = intent.getStringExtra(EXTRA_RECIPIENT_EMAIL)
        val currentUserEmail = intent.getStringExtra(EXTRA_CURRENT_USER_EMAIL)
        applicationComponent.plus(ChatModule(currentUserEmail, recipientEmail)).inject(this)
        presenter.init(currentUserEmail, recipientEmail)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbarPrimaryText.text = presenter.recipientEmail

        iconCall.setOnClickListener {
            presenter.initiateCall()
        }

        messageInput.setInputListener { input ->
            presenter.sendMessage(input.toString())
        }

        messagesAdapter = MessagesAdapter(presenter.currentUserEmail)
        messagesList.setAdapter(messagesAdapter)

        jitsiMeetView = getMeetView()

        presenter.openConnection()
    }

    private fun getMeetView() = JitsiMeetView(this).apply {
        hide()
        isPictureInPictureEnabled = false
        isWelcomePageEnabled = false
        loadURL(null)

        listener = object : JitsiMeetViewAdapter() {
            override fun onConferenceFailed(p0: MutableMap<String, Any>?) {

                jitsiMeetView.hide()
            }

            override fun onConferenceLeft(data: MutableMap<String, Any>?) {
                jitsiMeetView.hide()
            }
        }

        activityRoot.addView(this, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
    }

    override fun onResume() {
        super.onResume()
        ReactActivityLifecycleCallbacks.onHostResume(this)
    }

    public override fun onPause() {
        super.onPause()
        ReactActivityLifecycleCallbacks.onHostPause(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.closeConnection()
        jitsiMeetView.dispose()
        ReactActivityLifecycleCallbacks.onHostDestroy(this)
    }

    override fun onBackPressed() {
        ReactActivityLifecycleCallbacks.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun render(viewState: ViewState) {
        if (viewState.callUrl != null) {
            jitsiMeetView.loadURL(viewState.callUrl)
            jitsiMeetView.show()
        } else {
            jitsiMeetView.hide()
        }

        toolbarSecondaryText.text = when (viewState.messagesStatus) {
            ChatPresenter.MessagesStatus.WAITING_FOR_CONNECTION -> "Ожидание подключения..."
            ChatPresenter.MessagesStatus.UPDATING -> "Обновление..."
            ChatPresenter.MessagesStatus.UP_TO_DATE -> "Подключено"
        }
        messagesAdapter.setMessages(viewState.messages) {
            messagesList.layoutManager?.scrollToPosition(0)
        }
    }

    override fun requestPermissions(permissions: Array<out String>?, requestCode: Int, listener: PermissionListener?) {
        ReactActivityLifecycleCallbacks.requestPermissions(this, permissions, requestCode, listener)
    }

    override fun showEvent(event: Event) {
        when (event) {
            is Event.ShowException -> toast(event.throwable.toString())
            is Event.ShowNetworkException -> toast("Ошибка соединения")
            is Event.ShowSessionException -> onSessionException()
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