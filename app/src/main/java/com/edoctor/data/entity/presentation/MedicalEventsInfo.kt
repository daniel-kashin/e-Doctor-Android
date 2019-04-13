package com.edoctor.data.entity.presentation

import com.edoctor.data.entity.remote.model.record.MedicalEventModel

class MedicalEventsInfo(
    val medicalEvents: List<MedicalEventModel>,
    val availableMedicalEventTypes: List<MedicalEventType>
)