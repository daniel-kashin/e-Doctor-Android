package com.edoctor.presentation.app.findDoctor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.edoctor.R
import com.edoctor.presentation.app.chat.ChatActivity
import com.edoctor.utils.SessionExceptionHelper.onSessionException
import com.edoctor.utils.session

class FindDoctorFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pick_doctor, container, false).also { view ->
            val editText = view.findViewById<EditText>(R.id.edit_text)
            val button = view.findViewById<Button>(R.id.button)

            button.setOnClickListener {
                activity?.let { activity ->
                    activity.session.runIfOpened { sessionInfo ->
                        ChatActivity.IntentBuilder(this)
                            .recipientEmail(editText.text.toString())
                            .currentUserEmail(sessionInfo.account.email)
                            .start()
                    } ?: run {
                        activity.onSessionException()
                    }
                }
            }

        }
    }

}