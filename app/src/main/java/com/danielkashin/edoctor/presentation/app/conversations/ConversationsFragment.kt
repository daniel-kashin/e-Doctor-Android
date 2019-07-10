package com.danielkashin.edoctor.presentation.app.conversations

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.danielkashin.edoctor.R
import com.danielkashin.edoctor.data.entity.presentation.Conversation
import com.danielkashin.edoctor.data.entity.remote.model.user.UserModel
import com.danielkashin.edoctor.data.injection.ApplicationComponent
import com.danielkashin.edoctor.data.injection.ConversationsModule
import com.danielkashin.edoctor.presentation.app.chat.ChatActivity
import com.danielkashin.edoctor.presentation.app.conversations.ConversationsPresenter.Event
import com.danielkashin.edoctor.presentation.app.conversations.ConversationsPresenter.ViewState
import com.danielkashin.edoctor.presentation.architecture.fragment.BaseFragment
import com.danielkashin.edoctor.utils.*
import com.danielkashin.edoctor.utils.SessionExceptionHelper.onSessionException
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.dialogs.DialogsList
import javax.inject.Inject

class ConversationsFragment : BaseFragment<ConversationsPresenter, ViewState, Event>("ConversationsFragment") {

    companion object {
        private const val EXTRA_CURRENT_USER = "CURRENT_USER"

        fun newInstance(currentUser: UserModel) = ConversationsFragment().apply {
            arguments = Bundle().apply {
                putSerializable(EXTRA_CURRENT_USER, currentUser)
            }
        }
    }

    @Inject
    override lateinit var presenter: ConversationsPresenter

    override val layoutRes: Int = R.layout.fragment_conversations

    private lateinit var dialogsList: DialogsList
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var errorMessage: TextView
    private lateinit var dialogsAdapter: DialogsAdapter<Conversation>

    override fun init(applicationComponent: ApplicationComponent) {
        val currentUser = arguments!!.getSerializable(EXTRA_CURRENT_USER) as UserModel
        applicationComponent.plus(ConversationsModule(currentUser)).inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogsAdapter = DialogsAdapter(
            ImageLoader { imageView, url, _ ->
                PicassoProvider.get(imageView.context)
                    .load(url)
                    .fit()
                    .centerCrop()
                    .placeholder(R.color.lightLightGrey)
                    .into(imageView)
            }
        )
        context?.let { context ->
            dialogsAdapter.setOnDialogClickListener { dialog ->
                ChatActivity.IntentBuilder(context)
                    .recipientUser(dialog.recipientUser)
                    .currentUser(dialog.currentUser)
                    .start()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener { presenter.onReloadConversations() }
        dialogsList = view.findViewById(R.id.dialogs_list)
        errorMessage = view.findViewById(R.id.error_message)
        dialogsList.setAdapter(dialogsAdapter)
    }

    override fun render(viewState: ViewState) {
        swipeRefreshLayout.isRefreshing = viewState.isLoading
        dialogsAdapter.setDialogs(viewState.conversations.orEmpty()) {
            dialogsList.layoutManager?.scrollToPosition(0)
        }
        if (viewState.conversations?.isEmpty() == true) {
            errorMessage.show()
            errorMessage.setText(R.string.empty_conversations)
        } else {
            errorMessage.hide()
        }
    }

    override fun showEvent(event: Event) {
        when (event) {
            is Event.ShowUnknownException -> context.toast(getString(R.string.unhandled_error_message))
            is Event.ShowNoNetworkException -> context.toast(getString(R.string.network_error_message))
            is Event.ShowSessionException -> activity?.onSessionException()
        }
    }

}