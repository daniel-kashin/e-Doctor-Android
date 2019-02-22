package com.edoctor.presentation.app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.edoctor.R
import com.edoctor.utils.session

class FindDoctorFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pick_doctor, container, false).also { view ->
            val editText = view.findViewById<EditText>(R.id.edit_text)
            val button = view.findViewById<Button>(R.id.button)

            button.setOnClickListener {
                context?.session?.runIfOpened { sessionInfo ->
                    ChatActivity.IntentBuilder(this)
                        .recipientEmail(editText.text.toString())
                        .senderEmail(sessionInfo.account.email)
                        .start()
                }
            }

        }
    }

}