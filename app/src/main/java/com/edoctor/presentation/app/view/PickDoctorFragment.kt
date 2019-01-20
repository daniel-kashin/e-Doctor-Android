package com.edoctor.presentation.app.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.edoctor.EDoctor
import com.edoctor.R

class PickDoctorFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pick_doctor, container, false).also { view ->
            val editText = view.findViewById<EditText>(R.id.edit_text)
            val button = view.findViewById<Button>(R.id.button)

            (activity?.application as? EDoctor)?.applicationComponent?.sessionManager?.runIfOpened { sessionInfo ->
                button.setOnClickListener {
                    ChatActivity.IntentBuilder(this)
                        .recipientEmail(editText.text.toString())
                        .senderEmail(sessionInfo.profile.email)
                        .start()
                }
            }
        }
    }

}