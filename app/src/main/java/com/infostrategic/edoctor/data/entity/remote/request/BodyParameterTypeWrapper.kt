package com.infostrategic.edoctor.data.entity.remote.request

data class BodyParameterTypeWrapper(
    val type: Int,
    val customModelName: String? = null,
    val customModelUnit: String? = null
)