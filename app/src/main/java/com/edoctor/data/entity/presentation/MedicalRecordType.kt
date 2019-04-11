package com.edoctor.data.entity.presentation

import java.io.Serializable

sealed class MedicalRecordType : Serializable

sealed class BodyParameterType : MedicalRecordType() {

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

sealed class MedicalEventType : MedicalRecordType() {

    object Analysis : MedicalEventType()

    object Allergy : MedicalEventType()

    object Note : MedicalEventType()

    object Vaccination : MedicalEventType()

    object Procedure : MedicalEventType()

    object DoctorVisit : MedicalEventType()

    object Sickness : MedicalEventType()

}