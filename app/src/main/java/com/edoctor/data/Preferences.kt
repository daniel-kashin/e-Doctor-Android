package com.edoctor.data

import com.edoctor.data.session.SessionInfo
import com.edoctor.utils.AbstractPreferences

object Preferences : AbstractPreferences("preferences") {

    var sessionInfo by SharedPreferenceNullableDelegate.create<SessionInfo>(name = "session_info")

    var lastSynchronizeParametersTimestamp by SharedPreferenceNullableDelegate.create<Long>(name = "last_synchronize_timestamp")

    var lastSynchronizeEventsTimestamp by SharedPreferenceNullableDelegate.create<Long>(name = "last_synchronize_events_timestamp")

}