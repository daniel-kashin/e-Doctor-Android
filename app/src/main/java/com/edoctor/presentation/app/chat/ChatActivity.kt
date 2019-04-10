package com.edoctor.presentation.app.chat

import android.content.Context
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
import com.edoctor.data.entity.remote.model.user.DoctorModel
import com.edoctor.data.entity.remote.model.user.PatientModel
import com.edoctor.data.entity.remote.model.user.UserModel
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.data.injection.ChatModule
import com.edoctor.presentation.app.chat.ChatPresenter.Event
import com.edoctor.presentation.app.chat.ChatPresenter.ViewState
import com.edoctor.presentation.app.doctor.DoctorActivity
import com.edoctor.presentation.app.patient.PatientActivity
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
import org.jitsi.meet.sdk.*
import java.net.URL
import javax.inject.Inject


class ChatActivity : BaseActivity<ChatPresenter, ViewState, Event>("ChatActivity"), JitsiMeetActivityInterface {

    companion object {
        private const val EXTRA_CURRENT_USER = "SENDER_USER"
        private const val EXTRA_RECIPIENT_USER = "RECIPIENT_USER"
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
        val recipientUser = intent.getSerializableExtra(EXTRA_RECIPIENT_USER) as UserModel
        val currentUser = intent.getSerializableExtra(EXTRA_CURRENT_USER) as UserModel
        applicationComponent.plus(ChatModule(currentUser, recipientUser)).inject(this)
        presenter.init(currentUser, recipientUser)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val recipientUser = presenter.recipientUser
        toolbarPrimaryText.text = when {
            recipientUser.fullName != null -> recipientUser.fullName
            recipientUser is DoctorModel -> getString(R.string.doctor).capitalize()
            recipientUser is PatientModel -> getString(R.string.patient).capitalize()
            else -> null
        }
        toolbarPrimaryText.setOnClickListener {
            when(recipientUser) {
                is DoctorModel -> {
                    DoctorActivity.IntentBuilder(this)
                        .doctor(recipientUser)
                        .start()
                }
                is PatientModel -> {
                    PatientActivity.IntentBuilder(this)
                        .patient(recipientUser)
                        .start()
                }
            }
        }

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
        messagesAdapter = MessagesAdapter(presenter.currentUser.email, holdersConfig)
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

        listener = object : JitsiMeetViewListener {
            override fun onConferenceTerminated(p0: MutableMap<String, Any>?) {
                presenter.leaveCall()
            }
            override fun onConferenceJoined(p0: MutableMap<String, Any>?) {}
            override fun onConferenceWillJoin(p0: MutableMap<String, Any>?) {}
        }

        activityRoot.addView(this, ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT))
    }

    override fun onResume() {
        super.onResume()
        JitsiMeetActivityDelegate.onHostResume(this)
    }

    public override fun onPause() {
        super.onPause()
        JitsiMeetActivityDelegate.onHostPause(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        jitsiMeetView.dispose()
        JitsiMeetActivityDelegate.onHostDestroy(this)
    }

    override fun onBackPressed() {
        JitsiMeetActivityDelegate.onBackPressed()
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
                        presenter.recipientUser.email,
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
        jitsiMeetView.join(
            JitsiMeetConferenceOptions.Builder()
                .setAudioMuted(!callingView.isAudioEnabled)
                .setVideoMuted(!callingView.isVideoEnabled)
                .setServerURL(URL("https://meet.jit.si"))
                .setRoom(callStatusMessage.callUuid)
                .setWelcomePageEnabled(false)
                .build()
        )
        jitsiMeetView.show()
    }

    private fun hideJitsiMeetView() {
        jitsiMeetView.leave()
        jitsiMeetView.invisible()
    }

    override fun requestPermissions(permissions: Array<out String>?, requestCode: Int, listener: PermissionListener?) {
        JitsiMeetActivityDelegate.requestPermissions(this, permissions, requestCode, listener)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun showEvent(event: Event) {
        when (event) {
            is Event.ShowException -> toast(event.throwable.toString())
            is Event.ShowNetworkException -> toast(getString(R.string.network_error_message))
            is Event.ShowSessionException -> onSessionException()
        }
    }

    class IntentBuilder(context: Context) : CheckedIntentBuilder(context) {

        private var currentUser: UserModel? = null
        private var recipientUser: UserModel? = null

        fun recipientUser(recipientUser: UserModel) = apply { this.recipientUser = recipientUser }
        fun currentUser(currentUser: UserModel) = apply { this.currentUser = currentUser }

        override fun areParamsValid() = currentUser != null && recipientUser != null

        override fun get() = Intent(context, ChatActivity::class.java)
            .putExtra(EXTRA_RECIPIENT_USER, recipientUser)
            .putExtra(EXTRA_CURRENT_USER, currentUser)

    }

}