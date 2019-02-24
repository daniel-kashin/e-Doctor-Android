package com.edoctor.presentation.app.conversations

import android.os.Bundle
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.edoctor.R
import com.edoctor.data.entity.presentation.Conversation
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.data.injection.ConversationsModule
import com.edoctor.presentation.app.chat.ChatActivity
import com.edoctor.presentation.app.conversations.ConversationsPresenter.Event
import com.edoctor.presentation.app.conversations.ConversationsPresenter.ViewState
import com.edoctor.presentation.architecture.fragment.BaseFragment
import com.edoctor.utils.DialogsAdapter
import com.edoctor.utils.SessionExceptionHelper.onSessionException
import com.edoctor.utils.session
import com.edoctor.utils.toast
import com.stfalcon.chatkit.dialogs.DialogsList
import javax.inject.Inject

class ConversationsFragment : BaseFragment<ConversationsPresenter, ViewState, Event>("ConversationsFragment") {

    companion object {
        // TODO: replace with id
        const val EXTRA_CURRENT_USER_EMAIL = "CURRENT_USER_EMAIL"

        fun newInstance(currentUserEmail: String) = ConversationsFragment().apply {
            arguments = Bundle().apply {
                putString(EXTRA_CURRENT_USER_EMAIL, currentUserEmail)
            }
        }
    }

    @Inject
    override lateinit var presenter: ConversationsPresenter

    override val layoutRes: Int = R.layout.fragment_conversations

    private lateinit var dialogsList: DialogsList
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var dialogsAdapter: DialogsAdapter<Conversation>

    override fun init(applicationComponent: ApplicationComponent) {
        val currentUserEmail = arguments!!.getString(EXTRA_CURRENT_USER_EMAIL)!!
        applicationComponent.plus(ConversationsModule(currentUserEmail)).inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogsAdapter = DialogsAdapter()
        dialogsAdapter.setOnDialogClickListener {
            activity?.let { activity ->
                activity.session.runIfOpened { sessionInfo ->
                    ChatActivity.IntentBuilder(this)
                        .recipientEmail(it.dialogName)
                        .currentUserEmail(sessionInfo.account.email)
                        .start()
                } ?: run {
                    activity.onSessionException()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener { presenter.onReloadConversations() }
        dialogsList = view.findViewById(R.id.dialogs_list)
        dialogsList.setAdapter(dialogsAdapter)
    }

    override fun render(viewState: ViewState) {
        swipeRefreshLayout.isRefreshing = viewState.isLoading
        dialogsAdapter.setDialogs(viewState.conversations) {
            dialogsList.layoutManager?.scrollToPosition(0)
        }
    }

    override fun showEvent(event: Event) {
        when (event) {
            is Event.ShowUnknownException -> context.toast(event.throwable.toString())
            is Event.ShowNoNetworkException -> context.toast("Нет интернета")
            is Event.ShowSessionException -> activity?.onSessionException()
        }
    }

}