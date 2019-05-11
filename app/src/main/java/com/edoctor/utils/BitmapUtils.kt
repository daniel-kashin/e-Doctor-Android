package com.edoctor.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.File
import java.io.FileOutputStream

fun Bitmap.writeToFile(file: File): File {
    FileOutputStream(file).use {
        compress(Bitmap.CompressFormat.PNG, 100, it)
    }
    return file
}