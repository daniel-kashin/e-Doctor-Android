package com.danielkashin.edoctor.data.entity.remote.model.record

data class MedicalEventWrapper(
    val uuid: String,
    val timestamp: Long,
    val type: Int,
    val isDeleted: Boolean,
    val doctorCreatorUuid: String?,
    val isAddedFromDoctor: Boolean,
    val endTimestamp: Long? = null,
    val name: String? = null,
    val clinic: String? = null,
    val doctorName: String? = null,
    val doctorSpecialization: String? = null,
    val symptoms: String? = null,
    val diagnosis: String? = null,
    val recipe: String? = null,
    val comment: String? = null
)