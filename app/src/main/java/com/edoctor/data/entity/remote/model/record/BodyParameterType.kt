package com.edoctor.data.entity.remote.model.record

sealed class BodyParameterType {

    companion object {
        val NON_CUSTOM_TYPES = listOf(Height, Weight, BloodOxygen, BloodSugar, BloodPressure, Temperature)
    }

    object Height : BodyParameterType()

    object Weight : BodyParameterType()

    object BloodOxygen : BodyParameterType()

    object BloodSugar : BodyParameterType()

    object BloodPressure : BodyParameterType()

    object Temperature : BodyParameterType()

    class Custom(val name: String, val unit: String) : BodyParameterType() {
        companion object {
            val NEW = Custom("", "")
        }
    }

}