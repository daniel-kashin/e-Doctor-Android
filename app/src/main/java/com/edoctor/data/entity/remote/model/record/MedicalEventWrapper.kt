package com.edoctor.data.entity.remote.model.record

data class MedicalEventWrapper(
    val uuid: String,
    val timestamp: Long,
    val type: Int,
    val endTimestamp: Long? = null,
    val name: String? = null,
    val clinic: String? = null,
    val doctorName: String? = null,
    val doctorSpecialization: String? = null,
    val symptoms: String? = null,
    val diagnosis: String? = null,
    val recipe: String? = null,
    val comment: String? = null
) {
    companion object {
        const val TYPE_ANALYSIS = 0
        const val TYPE_ALLERGY = 1
        const val TYPE_NOTE = 2
        const val TYPE_VACCINATION = 3
        const val TYPE_PROCEDURE = 4
        const val TYPE_DOCTOR_VISIT = 5
        const val TYPE_SICKNESS = 6
    }
}