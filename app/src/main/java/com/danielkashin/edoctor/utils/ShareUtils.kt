package com.danielkashin.edoctor.utils

import android.content.Context
import android.content.Intent

object ShareUtils {

    fun shareText(text: String, subject: String, chooserTitle: String, context: Context) {
        val sharingIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(sharingIntent, chooserTitle))
    }

}