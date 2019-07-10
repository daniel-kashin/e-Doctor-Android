package com.infostrategic.edoctor.utils

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

fun Bitmap.writeToFile(file: File): File {
    FileOutputStream(file).use {
        compress(Bitmap.CompressFormat.PNG, 100, it)
    }
    return file
}