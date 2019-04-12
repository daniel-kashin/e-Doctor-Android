package com.edoctor.data.entity.presentation

import java.io.Serializable

sealed class MedicalRecordType : Serializable

sealed class BodyParameterType : MedicalRecordType() {

    companion object {
        val NON_CUSTOM_BODY_PARAMETER_TYPES = listOf(
            BodyParameterType.Height,
            BodyParameterType.Weight,
            BodyParameterType.BloodOxygen,
            BodyParameterType.BloodSugar,
            BodyParameterType.BloodPressure,
            BodyParameterType.Temperature
        )
    }

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

    companion object {
        val ALL_MEDICAL_EVENT_TYPES = listOf(
            MedicalEventType.Analysis,
            MedicalEventType.Allergy,
            MedicalEventType.DoctorVisit,
            MedicalEventType.Note,
            MedicalEventType.Procedure,
            MedicalEventType.Sickness,
            MedicalEventType.Vaccination
        )
    }

    object Analysis : MedicalEventType()

    object Allergy : MedicalEventType()

    object Note : MedicalEventType()

    object Vaccination : MedicalEventType()

    object Procedure : MedicalEventType()

    object DoctorVisit : MedicalEventType()

    object Sickness : MedicalEventType()

}