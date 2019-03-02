package com.edoctor.presentation.app.view

import android.os.Bundle
import android.os.PersistableBundle
import org.jitsi.meet.sdk.JitsiMeetActivity
import java.net.URL

class CallActivity : JitsiMeetActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        isPictureInPictureEnabled = true
        defaultURL = URL("https://meet.jit.si/eDoctorTest")
        super.onCreate(savedInstanceState, persistentState)
    }

}