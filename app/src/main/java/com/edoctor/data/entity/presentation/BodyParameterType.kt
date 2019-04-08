package com.edoctor.data.entity.presentation

import java.io.Serializable

sealed class BodyParameterType : Serializable {

    object Height : BodyParameterType()

    object Weight : BodyParameterType()

    object BloodOxygen : BodyParameterType()

    object BloodSugar : BodyParameterType()

    object BloodPressure : BodyParameterType()

    object Temperature : BodyParameterType()

    data class Custom(val name: String, val unit: String) : BodyParameterType() {
        companion object {
            val NEW = Custom("", "")
        }
    }

}