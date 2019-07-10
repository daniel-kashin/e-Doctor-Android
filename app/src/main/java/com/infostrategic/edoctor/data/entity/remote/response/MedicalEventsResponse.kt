package com.infostrategic.edoctor.data.entity.remote.response

import com.infostrategic.edoctor.data.entity.remote.model.record.MedicalEventWrapper

data class MedicalEventsResponse(
    val medicalEvents: List<MedicalEventWrapper>
)