package com.infostrategic.edoctor.data.entity.presentation

import com.infostrategic.edoctor.data.entity.remote.model.record.MedicalEventModel

class MedicalEventsInfo(
    val medicalEvents: List<MedicalEventModel>,
    val availableMedicalEventTypes: List<MedicalEventType>,
    val isSynchronized: Boolean
)