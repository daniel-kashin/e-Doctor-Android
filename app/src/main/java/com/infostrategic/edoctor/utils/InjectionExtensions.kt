package com.infostrategic.edoctor.utils

import android.content.Context
import com.infostrategic.edoctor.EDoctor

val Context.session
    get() = EDoctor.get(this).applicationComponent.sessionManager

