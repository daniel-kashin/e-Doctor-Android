package com.edoctor.data.entity.presentation

import com.edoctor.data.entity.presentation.MedicalEventType.Companion.ALL_MEDICAL_EVENT_TYPES
import com.edoctor.data.entity.remote.model.record.MedicalEventModel

class MedicalEventsInfo(
    val medicalEvents: List<MedicalEventModel>,
    val availableMedicalEventTypes: List<MedicalEventType>
) {
    companion object {
        val EMPTY = MedicalEventsInfo(emptyList(), ALL_MEDICAL_EVENT_TYPES)
    }
}