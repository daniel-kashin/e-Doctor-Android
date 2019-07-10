package com.infostrategic.edoctor.data.entity.remote.model.record

abstract class MedicalRecordModel {
    abstract val uuid: String
}

interface DateSpecific {
    val timestamp: Long
}

interface EndDateSpecific {
    val endTimestamp: Long?
}

interface ClinicSpecific {
    val clinic: String?
}

interface DoctorSpecific {
    val doctorName: String?
    val doctorSpecialization: String?
}

interface Commentable {
    val comment: String?
}

interface CreatableByDoctor {
    val doctorCreatorUuid: String?
    val isAddedFromDoctor: Boolean

    fun getAddedFromDoctorCopy(): CreatableByDoctor
}

interface Deletable {
    val isDeleted: Boolean
}

interface DocumentAttachable {
    val documents: List<String>
}

interface Remindable {
    val remindTimestamp: Long?
}