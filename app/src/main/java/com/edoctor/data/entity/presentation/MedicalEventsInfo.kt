package com.edoctor.data.entity.presentation

import com.edoctor.data.entity.remote.model.record.MedicalEventModel

class MedicalEventsInfo(
    val medicalEvents: List<MedicalEventModel>,
    val availableMedicalEventTypes: List<MedicalEventType>
) {
    companion object {
        val ALL_MEDICAL_EVENT_TYPES =  listOf(
            MedicalEventType.Analysis,
            MedicalEventType.Allergy,
            MedicalEventType.DoctorVisit,
            MedicalEventType.Note,
            MedicalEventType.Procedure,
            MedicalEventType.Sickness,
            MedicalEventType.Vaccination
        )

        val EMPTY = MedicalEventsInfo(emptyList(), ALL_MEDICAL_EVENT_TYPES)
    }
}