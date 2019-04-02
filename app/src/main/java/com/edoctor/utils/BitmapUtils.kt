package com.edoctor.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.File
import java.io.FileOutputStream

fun getBitmapFromString(base64String: String): Bitmap {
    val decodedString = Base64.decode(base64String, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
}

fun Bitmap.writeToFile(file: File): File {
    FileOutputStream(file).use {
        compress(Bitmap.CompressFormat.PNG, 100, it)
    }
    return file
}