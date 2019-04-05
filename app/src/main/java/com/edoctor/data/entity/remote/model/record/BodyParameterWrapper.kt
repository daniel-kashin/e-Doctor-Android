package com.edoctor.data.entity.remote.model.record

data class BodyParameterWrapper(
    val uuid: String,
    val measurementTimestamp: Long,
    val type: Int,
    val firstValue: Double,
    val secondValue: Double?,
    val customModelName: String?,
    val customModelUnit: String?
) {
    companion object {
        const val TYPE_CUSTOM = 0
        const val TYPE_HEIGHT = 1
        const val TYPE_WEIGHT = 2
        const val TYPE_BLOOD_PRESSURE = 3
        const val TYPE_BLOOD_SUGAR = 4
        const val TYPE_BLOOD_OXYGEN = 5
        const val TYPE_TEMPERATURE = 6
    }
}