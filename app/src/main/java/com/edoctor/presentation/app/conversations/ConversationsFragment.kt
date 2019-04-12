package com.edoctor.presentation.app.conversations

import android.os.Bundle
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.edoctor.R
import com.edoctor.data.entity.presentation.Conversation
import com.edoctor.data.entity.remote.model.user.UserModel
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.data.injection.ConversationsModule
import com.edoctor.presentation.app.chat.ChatActivity
import com.edoctor.presentation.app.conversations.ConversationsPresenter.Event
import com.edoctor.presentation.app.conversations.ConversationsPresenter.ViewState
import com.edoctor.presentation.architecture.fragment.BaseFragment
import com.edoctor.utils.DialogsAdapter
import com.edoctor.utils.SessionExceptionHelper.onSessionException
import com.edoctor.utils.toast
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
    private lateinit var dialogsAdapter: DialogsAdapter<Conversation>

    override fun init(applicationComponent: ApplicationComponent) {
        val currentUser = arguments!!.getSerializable(EXTRA_CURRENT_USER) as UserModel
        applicationComponent.plus(ConversationsModule(currentUser)).inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogsAdapter = DialogsAdapter(
            ImageLoader { imageView, url, _ ->
                Glide.with(imageView.context)
                    .load(url)
                    .apply(
                        RequestOptions()
                            .centerCrop()
                            .placeholder(R.color.lightLightGrey)
                            .dontAnimate()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                    )
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
            is Event.ShowUnknownException -> context.toast(getString(R.string.unhandled_error_message))
            is Event.ShowNoNetworkException -> context.toast(getString(R.string.network_error_message))
            is Event.ShowSessionException -> activity?.onSessionException()
        }
    }

}