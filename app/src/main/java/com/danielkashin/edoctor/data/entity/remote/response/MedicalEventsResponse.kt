package com.danielkashin.edoctor.data.entity.remote.response

import com.danielkashin.edoctor.data.entity.remote.model.record.MedicalEventWrapper

data class MedicalEventsResponse(
    val medicalEvents: List<MedicalEventWrapper>
)