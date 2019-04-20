package com.edoctor.data.entity.remote.model.record

import java.io.Serializable

sealed class MedicalEventModel : MedicalRecordModel(), DateSpecific, Deletable, Commentable, Serializable, CreatableByDoctor {
    abstract override fun getAddedFromDoctorCopy(): MedicalEventModel
}

data class Analysis(
    override val uuid: String,
    override val isDeleted: Boolean,
    override val doctorCreatorUuid: String? = null,
    override var isAddedFromDoctor: Boolean = false,
    override val timestamp: Long,
    override val comment: String?,
    override val clinic: String?,
    val name: String,
    val result: String?
) : MedicalEventModel(), ClinicSpecific {

    override fun getAddedFromDoctorCopy() = copy(isAddedFromDoctor = true)

}

data class Allergy(
    override val uuid: String,
    override val isDeleted: Boolean,
    override val doctorCreatorUuid: String? = null,
    override var isAddedFromDoctor: Boolean = false,
    override val timestamp: Long,
    override val comment: String?,
    override val endTimestamp: Long?,
    val allergenName: String,
    val symptoms: String?
) : MedicalEventModel(), EndDateSpecific {

    override fun getAddedFromDoctorCopy() = copy(isAddedFromDoctor = true)

}


data class Note(
    override val uuid: String,
    override val isDeleted: Boolean,
    override val doctorCreatorUuid: String? = null,
    override var isAddedFromDoctor: Boolean = false,
    override val timestamp: Long,
    override val comment: String?
) : MedicalEventModel() {

    override fun getAddedFromDoctorCopy() = copy(isAddedFromDoctor = true)

}


data class Vaccination(
    override val uuid: String,
    override val isDeleted: Boolean,
    override val doctorCreatorUuid: String? = null,
    override var isAddedFromDoctor: Boolean = false,
    override val timestamp: Long,
    override val comment: String?,
    override val clinic: String?,
    override val doctorName: String?,
    override val doctorSpecialization: String?,
    val name: String
) : MedicalEventModel(), ClinicSpecific, DoctorSpecific {

    override fun getAddedFromDoctorCopy() = copy(isAddedFromDoctor = true)

}


data class Procedure(
    override val uuid: String,
    override val isDeleted: Boolean,
    override val doctorCreatorUuid: String? = null,
    override var isAddedFromDoctor: Boolean = false,
    override val timestamp: Long,
    override val comment: String?,
    override val clinic: String?,
    override val doctorName: String?,
    override val doctorSpecialization: String?,
    val name: String
) : MedicalEventModel(), ClinicSpecific, DoctorSpecific {

    override fun getAddedFromDoctorCopy() = copy(isAddedFromDoctor = true)

}


data class DoctorVisit(
    override val uuid: String,
    override val isDeleted: Boolean,
    override val doctorCreatorUuid: String? = null,
    override var isAddedFromDoctor: Boolean = false,
    override val timestamp: Long,
    override val comment: String?,
    override val clinic: String?,
    override val doctorName: String?,
    override val doctorSpecialization: String?,
    val complaints: String,
    val diagnosisAndRecommendations: String,
    val recipe: String?
) : MedicalEventModel(), ClinicSpecific, DoctorSpecific {

    override fun getAddedFromDoctorCopy() = copy(isAddedFromDoctor = true)

}


data class Sickness(
    override val uuid: String,
    override val isDeleted: Boolean,
    override val doctorCreatorUuid: String? = null,
    override var isAddedFromDoctor: Boolean = false,
    override val timestamp: Long,
    override val comment: String?,
    override val endTimestamp: Long?,
    val symptoms: String?,
    val diagnosis: String
) : MedicalEventModel(), EndDateSpecific {

    override fun getAddedFromDoctorCopy() = copy(isAddedFromDoctor = true)

}
