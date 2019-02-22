package com.edoctor.data.session

import com.edoctor.utils.AbstractPreferences

object SessionPreferences : AbstractPreferences("session_preferences") {
    var sessionInfo by SharedPreferenceNullableDelegate.create<SessionInfo>(name = "session_info")
}