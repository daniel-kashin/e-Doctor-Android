package com.edoctor.utils

import android.content.Context
import com.edoctor.EDoctor

val Context.session
    get() = EDoctor.get(this).applicationComponent.sessionManager

