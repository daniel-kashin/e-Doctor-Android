package com.infostrategic.edoctor.data.entity.remote.model.record

data class SynchronizeEventsModel(
    val events: List<MedicalEventWrapper>,
    val synchronizeTimestamp: Long
)