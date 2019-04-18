package com.edoctor.data

import com.edoctor.data.session.SessionInfo
import com.edoctor.utils.AbstractPreferences

object Preferences : AbstractPreferences("preferences") {

    fun clearUserData() {

    }

    var sessionInfo by SharedPreferenceNullableDelegate.create<SessionInfo>(name = "session_info")

}