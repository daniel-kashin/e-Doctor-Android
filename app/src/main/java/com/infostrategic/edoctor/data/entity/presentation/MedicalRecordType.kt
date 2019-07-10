package com.infostrategic.edoctor.data.entity.presentation

import java.io.Serializable

sealed class MedicalRecordType : Serializable

sealed class BodyParameterType : MedicalRecordType() {

    companion object {
        val NON_CUSTOM_BODY_PARAMETER_TYPES = listOf(
            BodyParameterType.Height(),
            BodyParameterType.Weight(),
            BodyParameterType.BloodOxygen(),
            BodyParameterType.BloodSugar(),
            BodyParameterType.BloodPressure(),
            BodyParameterType.Temperature()
        )
    }

    class Height : BodyParameterType()

    class Weight : BodyParameterType()

    class BloodOxygen : BodyParameterType()

    class BloodSugar : BodyParameterType()

    class BloodPressure : BodyParameterType()

    class Temperature : BodyParameterType()

    data class Custom(val name: String, val unit: String) : BodyParameterType() {
        companion object {
            val NEW = Custom("", "")
        }
    }

}

sealed class MedicalEventType : MedicalRecordType() {

    companion object {
        val ALL_MEDICAL_EVENT_TYPES = listOf(
            MedicalEventType.Analysis(),
            MedicalEventType.Allergy(),
            MedicalEventType.DoctorVisit(),
            MedicalEventType.Note(),
            MedicalEventType.Procedure(),
            MedicalEventType.Sickness(),
            MedicalEventType.Vaccination()
        )
    }

    class Analysis : MedicalEventType()

    class Allergy : MedicalEventType()

    class Note : MedicalEventType()

    class Vaccination : MedicalEventType()

    class Procedure : MedicalEventType()

    class DoctorVisit : MedicalEventType()

    class Sickness : MedicalEventType()

}