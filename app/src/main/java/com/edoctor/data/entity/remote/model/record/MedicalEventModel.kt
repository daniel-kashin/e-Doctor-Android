package com.edoctor.data.entity.remote.model.record

import java.io.Serializable

sealed class MedicalEventModel : MedicalRecordModel(), DateSpecific, Commentable, Serializable, CreatableByDoctor

data class Analysis(
    override val uuid: String,
    override val doctorCreatorUuid: String? = null, // TODO
    override val isAddedFromDoctor: Boolean = false, // TODO
    override val timestamp: Long,
    override val comment: String?,
    override val clinic: String?,
    val name: String,
    val result: String?
) : MedicalEventModel(), ClinicSpecific

data class Allergy(
    override val uuid: String,
    override val doctorCreatorUuid: String? = null, // TODO
    override val isAddedFromDoctor: Boolean = false, // TODO
    override val timestamp: Long,
    override val comment: String?,
    override val endTimestamp: Long?,
    val allergenName: String,
    val symptoms: String?
) : MedicalEventModel(), EndDateSpecific

data class Note(
    override val uuid: String,
    override val doctorCreatorUuid: String? = null, // TODO
    override val isAddedFromDoctor: Boolean = false, // TODO
    override val timestamp: Long,
    override val comment: String?
) : MedicalEventModel()

data class Vaccination(
    override val uuid: String,
    override val doctorCreatorUuid: String? = null, // TODO
    override val isAddedFromDoctor: Boolean = false, // TODO
    override val timestamp: Long,
    override val comment: String?,
    override val clinic: String?,
    override val doctorName: String?,
    override val doctorSpecialization: String?,
    val name: String
) : MedicalEventModel(), ClinicSpecific, DoctorSpecific

data class Procedure(
    override val uuid: String,
    override val doctorCreatorUuid: String? = null, // TODO
    override val isAddedFromDoctor: Boolean = false, // TODO
    override val timestamp: Long,
    override val comment: String?,
    override val clinic: String?,
    override val doctorName: String?,
    override val doctorSpecialization: String?,
    val name: String
) : MedicalEventModel(), ClinicSpecific, DoctorSpecific

data class DoctorVisit(
    override val uuid: String,
    override val doctorCreatorUuid: String? = null, // TODO
    override val isAddedFromDoctor: Boolean = false, // TODO
    override val timestamp: Long,
    override val comment: String?,
    override val clinic: String?,
    override val doctorName: String?,
    override val doctorSpecialization: String?,
    val complaints: String,
    val diagnosisAndRecommendations: String,
    val recipe: String?
) : MedicalEventModel(), ClinicSpecific, DoctorSpecific

data class Sickness(
    override val uuid: String,
    override val doctorCreatorUuid: String? = null, // TODO
    override val isAddedFromDoctor: Boolean = false, // TODO
    override val timestamp: Long,
    override val comment: String?,
    override val endTimestamp: Long?,
    val symptoms: String?,
    val diagnosis: String
) : MedicalEventModel(), EndDateSpecific