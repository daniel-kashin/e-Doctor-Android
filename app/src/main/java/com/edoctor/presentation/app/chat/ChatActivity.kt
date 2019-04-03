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
import com.edoctor.data.entity.presentation.CallStatusMessage
import com.edoctor.data.entity.presentation.CallStatusMessage.CallStatus.*
import com.edoctor.data.entity.presentation.Message
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.data.injection.ChatModule
import com.edoctor.presentation.app.chat.ChatPresenter.Event
import com.edoctor.presentation.app.chat.ChatPresenter.ViewState
import com.edoctor.presentation.architecture.activity.BaseActivity
import com.edoctor.presentation.views.CallMessageContentChecker
import com.edoctor.presentation.views.CallMessageContentChecker.Companion.CONTENT_TYPE_CALL
import com.edoctor.presentation.views.CallingView
import com.edoctor.presentation.views.CallingView.CallType.INCOMING
import com.edoctor.presentation.views.CallingView.CallType.OUTCOMING
import com.edoctor.presentation.views.IncomingCallMessageViewHolder
import com.edoctor.presentation.views.OutcomingCallMessageViewHolder
import com.edoctor.utils.*
import com.edoctor.utils.SessionExceptionHelper.onSessionException
import com.facebook.react.modules.core.PermissionListener
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesList
import org.jitsi.meet.sdk.JitsiMeetActivityInterface
import org.jitsi.meet.sdk.JitsiMeetView
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
    private val callingView by lazyFind<CallingView>(R.id.calling_view)
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

        callingView.onCallAcceptedListener = {
            presenter.acceptCall()
        }

        callingView.onCallDeclinedListener = {
            presenter.leaveCall()
        }

        messageInput.setInputListener { input ->
            presenter.sendMessage(input.toString())
        }

        val holdersConfig = MessageHolders()
            .setIncomingTextLayout(R.layout.item_incoming_text_message)
            .setOutcomingTextLayout(R.layout.item_outcoming_text_message)
            .registerContentType(
                CONTENT_TYPE_CALL,
                IncomingCallMessageViewHolder::class.java, R.layout.item_incoming_call_message,
                OutcomingCallMessageViewHolder::class.java, R.layout.item_outcoming_call_message,
                CallMessageContentChecker()
            )
        messagesAdapter = MessagesAdapter(presenter.currentUserEmail, holdersConfig)
        messagesList.setAdapter(messagesAdapter)

        jitsiMeetView = getMeetView()
    }

    override fun onStart() {
        super.onStart()
        presenter.openConnection()
    }

    override fun onStop() {
        presenter.closeConnection()
        super.onStop()
    }

    private fun getMeetView() = JitsiMeetView(this).apply {
        invisible()
        isPictureInPictureEnabled = false
        isWelcomePageEnabled = false
        defaultURL = null
        loadURL(null)

        listener = object : JitsiMeetViewAdapter() {
            override fun onConferenceFailed(p0: MutableMap<String, Any>?) {
                presenter.leaveCall()
            }

            override fun onConferenceLeft(data: MutableMap<String, Any>?) {
                presenter.leaveCall()
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
        viewState.callStatusMessage.let { callStatusMessage ->
            when (callStatusMessage?.callStatus) {
                STARTED -> {
                    callingView.hide()
                    showJitsiMeetView(callStatusMessage)
                }
                INITIATED -> {
                    hideJitsiMeetView()
                    callingView.bind(
                        presenter.recipientEmail,
                        if (callStatusMessage.isFromCurrentUser) OUTCOMING else INCOMING
                    )
                    callingView.show()
                }
                CANCELLED, null -> {
                    callingView.hide()
                    hideJitsiMeetView()
                }
            }
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

    private fun showJitsiMeetView(callStatusMessage: CallStatusMessage) {
        val urlObject = Bundle().apply {
            putBundle("config", Bundle().apply {
                putBoolean("startWithAudioMuted", !callingView.isAudioEnabled)
                putBoolean("startWithVideoMuted", !callingView.isVideoEnabled)
            })
            putString("url", callStatusMessage.getCallUrl())
        }
        jitsiMeetView.loadURLObject(urlObject)
        jitsiMeetView.show()
    }

    private fun hideJitsiMeetView() {
        jitsiMeetView.loadURL(null)
        jitsiMeetView.invisible()
    }

    override fun requestPermissions(permissions: Array<out String>?, requestCode: Int, listener: PermissionListener?) {
        ReactActivityLifecycleCallbacks.requestPermissions(this, permissions, requestCode, listener)
    }

    override fun showEvent(event: Event) {
        when (event) {
            is Event.ShowException -> toast(event.throwable.toString())
            is Event.ShowNetworkException -> toast(getString(R.string.network_error_message))
            is Event.ShowSessionException -> onSessionException()
        }
    }

    private fun CallStatusMessage.getCallUrl() = "https://meet.jit.si/$callUuid"

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