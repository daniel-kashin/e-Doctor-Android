package com.edoctor.data.entity.remote.model.medicalAccess

import com.edoctor.data.entity.remote.model.user.DoctorModel
import com.edoctor.data.entity.remote.model.user.PatientModel

data class MedicalAccessesForDoctorModel(
    val medicalAccesses: List<MedicalAccessForDoctorModel>
)

data class MedicalAccessesForPatientModel(
    val medicalAccesses: List<MedicalAccessForPatientModel>,
    val allTypes: List<MedicalRecordTypeModel>
)

data class MedicalAccessForDoctorModel(
    val patient: PatientModel,
    val availableTypes: List<MedicalRecordTypeModel>
)

data class MedicalAccessForPatientModel(
    val doctor: DoctorModel,
    val availableTypes: List<MedicalRecordTypeModel>
)

data class MedicalRecordTypeModel(
    val medicalRecordType: Int,
    val customModelName: String? = null,
    val customModelUnit: String? = null
)