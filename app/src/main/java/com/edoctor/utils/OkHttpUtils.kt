package com.edoctor.utils

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Use it for images (instead of [asBodyPart]) due to '.tmp' extension of temp pictures.
 */
internal fun File.asImageBodyPart(partName: String): MultipartBody.Part {
    val body = RequestBody.create(MediaType.parse("image/*"), this)
    return MultipartBody.Part.createFormData(partName, name, body)
}