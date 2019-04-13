package com.edoctor.data.mapper

import com.edoctor.data.entity.presentation.BodyParameterType
import com.edoctor.data.entity.presentation.MedicalEventType
import com.edoctor.data.entity.presentation.MedicalRecordType
import com.edoctor.data.entity.remote.model.medicalAccess.MedicalRecordTypeModel

object MedicalRecordTypeMapper {

    private const val BODY_PARAMETERS_OFFSET = 0
    private const val MEDICAL_EVENTS_OFFSET = 10000

    const val BODY_PARAMETER_TYPE_CUSTOM = 0
    const val BODY_PARAMETER_TYPE_HEIGHT = 1
    const val BODY_PARAMETER_TYPE_WEIGHT = 2
    const val BODY_PARAMETER_TYPE_BLOOD_PRESSURE = 3
    const val BODY_PARAMETER_TYPE_BLOOD_SUGAR = 4
    const val BODY_PARAMETER_TYPE_BLOOD_OXYGEN = 5
    const val BODY_PARAMETER_TYPE_TEMPERATURE = 6

    const val MEDICAL_EVENT_TYPE_ANALYSIS = 0
    const val MEDICAL_EVENT_TYPE_ALLERGY = 1
    const val MEDICAL_EVENT_TYPE_NOTE = 2
    const val MEDICAL_EVENT_TYPE_VACCINATION = 3
    const val MEDICAL_EVENT_TYPE_PROCEDURE = 4
    const val MEDICAL_EVENT_TYPE_DOCTOR_VISIT = 5
    const val MEDICAL_EVENT_TYPE_SICKNESS = 6

    fun toModel(presentation: MedicalRecordType): MedicalRecordTypeModel? =
        when (presentation) {
            is BodyParameterType -> when (presentation) {
                is BodyParameterType.Height -> {
                    MedicalRecordTypeModel(BODY_PARAMETERS_OFFSET + BODY_PARAMETER_TYPE_HEIGHT)
                }
                is BodyParameterType.Weight -> {
                    MedicalRecordTypeModel(BODY_PARAMETERS_OFFSET + BODY_PARAMETER_TYPE_WEIGHT)
                }
                is BodyParameterType.BloodPressure -> {
                    MedicalRecordTypeModel(BODY_PARAMETERS_OFFSET + BODY_PARAMETER_TYPE_BLOOD_PRESSURE)
                }
                is BodyParameterType.BloodSugar -> {
                    MedicalRecordTypeModel(BODY_PARAMETERS_OFFSET + BODY_PARAMETER_TYPE_BLOOD_SUGAR)
                }
                is BodyParameterType.BloodOxygen -> {
                    MedicalRecordTypeModel(BODY_PARAMETERS_OFFSET + BODY_PARAMETER_TYPE_BLOOD_OXYGEN)
                }
                is BodyParameterType.Temperature -> {
                    MedicalRecordTypeModel(BODY_PARAMETERS_OFFSET + BODY_PARAMETER_TYPE_TEMPERATURE)
                }
                is BodyParameterType.Custom -> {
                    MedicalRecordTypeModel(
                        BODY_PARAMETERS_OFFSET + BODY_PARAMETER_TYPE_CUSTOM,
                        presentation.name,
                        presentation.unit
                    )
                }
            }
            is MedicalEventType -> when (presentation) {
                is MedicalEventType.Analysis -> {
                    MedicalRecordTypeModel(MEDICAL_EVENTS_OFFSET + MEDICAL_EVENT_TYPE_ANALYSIS)
                }
                is MedicalEventType.Allergy -> {
                    MedicalRecordTypeModel(MEDICAL_EVENTS_OFFSET + MEDICAL_EVENT_TYPE_ALLERGY)
                }
                is MedicalEventType.Note -> {
                    MedicalRecordTypeModel(MEDICAL_EVENTS_OFFSET + MEDICAL_EVENT_TYPE_NOTE)
                }
                is MedicalEventType.Vaccination -> {
                    MedicalRecordTypeModel(MEDICAL_EVENTS_OFFSET + MEDICAL_EVENT_TYPE_VACCINATION)
                }
                is MedicalEventType.Procedure -> {
                    MedicalRecordTypeModel(MEDICAL_EVENTS_OFFSET + MEDICAL_EVENT_TYPE_PROCEDURE)
                }
                is MedicalEventType.DoctorVisit -> {
                    MedicalRecordTypeModel(MEDICAL_EVENTS_OFFSET + MEDICAL_EVENT_TYPE_DOCTOR_VISIT)
                }
                is MedicalEventType.Sickness -> {
                    MedicalRecordTypeModel(MEDICAL_EVENTS_OFFSET + MEDICAL_EVENT_TYPE_SICKNESS)
                }
            }
        }

    fun toPresentation(model: MedicalRecordTypeModel): MedicalRecordType? =
        when (model.medicalRecordType) {
            BODY_PARAMETERS_OFFSET + BODY_PARAMETER_TYPE_HEIGHT -> BodyParameterType.Height()
            BODY_PARAMETERS_OFFSET + BODY_PARAMETER_TYPE_WEIGHT -> BodyParameterType.Weight()
            BODY_PARAMETERS_OFFSET + BODY_PARAMETER_TYPE_BLOOD_PRESSURE -> BodyParameterType.BloodPressure()
            BODY_PARAMETERS_OFFSET + BODY_PARAMETER_TYPE_BLOOD_SUGAR -> BodyParameterType.BloodSugar()
            BODY_PARAMETERS_OFFSET + BODY_PARAMETER_TYPE_BLOOD_OXYGEN -> BodyParameterType.BloodOxygen()
            BODY_PARAMETERS_OFFSET + BODY_PARAMETER_TYPE_TEMPERATURE -> BodyParameterType.Temperature()
            BODY_PARAMETERS_OFFSET + BODY_PARAMETER_TYPE_CUSTOM -> {
                if (model.customModelName != null && model.customModelUnit != null) {
                    BodyParameterType.Custom(model.customModelName, model.customModelUnit)
                } else {
                    null
                }
            }

            MEDICAL_EVENTS_OFFSET + MEDICAL_EVENT_TYPE_ANALYSIS -> MedicalEventType.Analysis()
            MEDICAL_EVENTS_OFFSET + MEDICAL_EVENT_TYPE_ALLERGY -> MedicalEventType.Allergy()
            MEDICAL_EVENTS_OFFSET + MEDICAL_EVENT_TYPE_NOTE -> MedicalEventType.Note()
            MEDICAL_EVENTS_OFFSET + MEDICAL_EVENT_TYPE_VACCINATION -> MedicalEventType.Vaccination()
            MEDICAL_EVENTS_OFFSET + MEDICAL_EVENT_TYPE_PROCEDURE -> MedicalEventType.Procedure()
            MEDICAL_EVENTS_OFFSET + MEDICAL_EVENT_TYPE_DOCTOR_VISIT -> MedicalEventType.DoctorVisit()
            MEDICAL_EVENTS_OFFSET + MEDICAL_EVENT_TYPE_SICKNESS -> MedicalEventType.Sickness()

            else -> null
        }

}