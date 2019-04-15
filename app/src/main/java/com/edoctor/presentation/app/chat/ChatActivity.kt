package com.edoctor.presentation.app.chat

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.edoctor.R
import com.edoctor.data.entity.presentation.*
import com.edoctor.data.entity.presentation.CallStatusMessage.CallStatus.*
import com.edoctor.data.entity.remote.model.user.DoctorModel
import com.edoctor.data.entity.remote.model.user.PatientModel
import com.edoctor.data.entity.remote.model.user.UserModel
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.data.injection.ChatModule
import com.edoctor.presentation.app.chat.ChatPresenter.Event
import com.edoctor.presentation.app.chat.ChatPresenter.ViewState
import com.edoctor.presentation.app.doctor.DoctorActivity
import com.edoctor.presentation.app.imageViewerActivity.ImageViewerActivity
import com.edoctor.presentation.app.patient.PatientActivity
import com.edoctor.presentation.app.recordsForPatient.RecordsForPatientActivity
import com.edoctor.presentation.app.recordsFromDoctor.RecordsFromDoctorActivity
import com.edoctor.presentation.architecture.activity.BaseActivity
import com.edoctor.presentation.views.*
import com.edoctor.presentation.views.MessageContentChecker.Companion.CONTENT_TYPE_CALL
import com.edoctor.presentation.views.MessageContentChecker.Companion.CONTENT_TYPE_HYPERLINK_TEXT
import com.edoctor.presentation.views.CallingView.CallType.INCOMING
import com.edoctor.presentation.views.CallingView.CallType.OUTCOMING
import com.edoctor.utils.*
import com.edoctor.utils.SessionExceptionHelper.onSessionException
import com.facebook.react.modules.core.PermissionListener
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesList
import com.tbruyelle.rxpermissions.RxPermissions
import org.jitsi.meet.sdk.*
import java.net.URL
import javax.inject.Inject


class ChatActivity : BaseActivity<ChatPresenter, ViewState, Event>("ChatActivity"), JitsiMeetActivityInterface {

    companion object {
        private const val EXTRA_CURRENT_USER = "SENDER_USER"
        private const val EXTRA_RECIPIENT_USER = "RECIPIENT_USER"

        private const val REQUEST_CAMERA = 10135
        private const val REQUEST_GALLERY = 10136
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

        val currentUser = presenter.currentUser
        val recipientUser = presenter.recipientUser

        toolbarPrimaryText.text = when {
            recipientUser.fullName != null -> recipientUser.fullName
            recipientUser is DoctorModel -> getString(R.string.doctor).capitalize()
            recipientUser is PatientModel -> getString(R.string.patient).capitalize()
            else -> null
        }
        toolbarPrimaryText.setOnClickListener {
            when (recipientUser) {
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

        iconCall.show(presenter.currentUser is DoctorModel)
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

        messageInput.setAttachmentsListener {
            showImagePickerOptions()
        }

        val holdersConfig = MessageHolders()
            .setIncomingImageLayout(R.layout.item_incoming_image_message)
            .setOutcomingImageLayout(R.layout.item_outcoming_image_message)
            .setIncomingTextLayout(R.layout.item_incoming_text_message)
            .setOutcomingTextLayout(R.layout.item_outcoming_text_message)
            .registerContentType(
                CONTENT_TYPE_CALL,
                IncomingCallMessageViewHolder::class.java, R.layout.item_incoming_call_message,
                OutcomingCallMessageViewHolder::class.java, R.layout.item_outcoming_call_message,
                MessageContentChecker()
            )
            .registerContentType(
                CONTENT_TYPE_HYPERLINK_TEXT,
                IncomingHyperlinkTextMessageViewHolder::class.java, R.layout.item_incoming_text_message,
                OutcomingHyperlinkTextMessageViewHolder::class.java, R.layout.item_outcoming_text_message,
                MessageContentChecker()
            )

        messagesAdapter = MessagesAdapter(
            presenter.currentUser.email,
            holdersConfig,
            ImageLoader { imageView, url, _ ->
                Glide.with(imageView.context)
                    .load(url)
                    .apply(
                        RequestOptions()
                            .centerCrop()
                            .placeholder(R.color.lightLightGrey)
                            .dontAnimate()
                    )
                    .into(imageView)
            }
        )
        messagesAdapter.setOnMessageClickListener { message ->
            when (message) {
                is MedicalAccessesMessage -> {
                    if (currentUser is PatientModel && recipientUser is DoctorModel) {
                        DoctorActivity.IntentBuilder(this)
                            .doctor(recipientUser)
                            .start()
                    }
                    if (currentUser is DoctorModel && recipientUser is PatientModel) {
                        PatientActivity.IntentBuilder(this)
                            .patient(recipientUser)
                            .start()
                    }
                }
                is MedicalRecordRequestMessage -> {
                    if (currentUser is PatientModel && recipientUser is DoctorModel) {
                        RecordsFromDoctorActivity.IntentBuilder(this)
                            .doctor(recipientUser)
                            .start()
                    }
                    if (currentUser is DoctorModel && recipientUser is PatientModel) {
                        RecordsForPatientActivity.IntentBuilder(this)
                            .patient(recipientUser)
                            .start()
                    }
                }
                is ImageMessage -> {
                    ImageViewerActivity.show(this, message.imageUrl)
                }
            }
        }

        messagesList.setAdapter(messagesAdapter)

        jitsiMeetView = getMeetView()

        presenter.openConnection()
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
        presenter.closeConnection()
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
            ChatPresenter.MessagesStatus.WAITING_FOR_CONNECTION -> "${getString(R.string.waiting_for_connection)}..."
            ChatPresenter.MessagesStatus.UPDATING -> "${getString(R.string.updating)}..."
            ChatPresenter.MessagesStatus.UP_TO_DATE -> getString(R.string.connected)
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
            is Event.ShowImageUploadStart -> toast(getString(R.string.image_upload_start))
            is Event.ShowImageUploadSuccess -> toast(getString(R.string.image_upload_success))
            is Event.ShowImageUploadException -> toast(getString(R.string.image_upload_exception))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA -> {
                    (data?.extras?.get("data") as? Bitmap)?.let {
                        presenter.onImageSelected(it, cacheDir)
                    }
                }
                REQUEST_GALLERY -> {
                    MediaStore.Images.Media.getBitmap(contentResolver, data?.data)?.let {
                        presenter.onImageSelected(it, cacheDir)
                    }
                }
            }
        }
    }

    private fun showImagePickerOptions() {
        PopupMenu(messageInput.context, messageInput).apply {
            menuInflater.inflate(R.menu.image_picker, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.gallery -> {
                        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
                        galleryIntent.type = Constants.PICK_IMAGE_INTENT_TYPE
                        startActivityForResult(
                            Intent.createChooser(galleryIntent, null),
                            REQUEST_GALLERY
                        )
                    }
                    R.id.camera -> {
                        RxPermissions.getInstance(this@ChatActivity)
                            .request(Manifest.permission.CAMERA)
                            .onErrorReturn { false }
                            .subscribe { granted ->
                                if (granted) {
                                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                    if (cameraIntent.resolveActivity(packageManager) != null) {
                                        startActivityForResult(cameraIntent, REQUEST_CAMERA)
                                    }
                                }
                            }
                    }
                }
                true
            }
            show()
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