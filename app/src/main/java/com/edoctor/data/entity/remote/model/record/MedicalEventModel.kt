package com.edoctor.data.entity.remote.model.record

sealed class MedicalEventModel : MedicalRecordModel(), DateSpecific, Commentable

data class Analysis(
    override val uuid: String,
    override val timestamp: Long,
    override val comment: String?,
    override val clinic: String?,
    val name: String,
    val result: String?
) : MedicalEventModel(), ClinicSpecific

data class Allergy(
    override val uuid: String,
    override val timestamp: Long,
    override val comment: String?,
    val endTimestamp: Long?,
    val allergenName: String?,
    val reaction: String?
) : MedicalEventModel()

data class Note(
    override val uuid: String,
    override val timestamp: Long,
    override val comment: String?
) : MedicalEventModel()

data class Vaccination(
    override val uuid: String,
    override val timestamp: Long,
    override val comment: String?,
    override val clinic: String?,
    override val doctorName: String?,
    override val doctorSpecialization: String?,
    val name: String
) : MedicalEventModel(), ClinicSpecific, DoctorSpecific

data class Procedure(
    override val uuid: String,
    override val timestamp: Long,
    override val comment: String?,
    override val clinic: String?,
    override val doctorName: String?,
    override val doctorSpecialization: String?,
    val name: String
) : MedicalEventModel(), ClinicSpecific, DoctorSpecific

data class DoctorVisit(
    override val uuid: String,
    override val timestamp: Long,
    override val comment: String?,
    override val clinic: String?,
    override val doctorName: String?,
    override val doctorSpecialization: String?,
    val diagnosisAndRecommendations: String?,
    val recipe: String?
) : MedicalEventModel(), ClinicSpecific, DoctorSpecific

data class Sickness(
    override val uuid: String,
    override val timestamp: Long,
    override val comment: String?,
    val endTimestamp: Long?,
    val symptoms: String?,
    val diagnosis: String
) : MedicalEventModel()