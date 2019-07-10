package com.danielkashin.edoctor.utils

import android.content.Context
import com.danielkashin.edoctor.EDoctor

val Context.session
    get() = EDoctor.get(this).applicationComponent.sessionManager

