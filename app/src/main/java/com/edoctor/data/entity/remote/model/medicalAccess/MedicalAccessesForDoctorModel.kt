package com.edoctor.data.entity.remote.model.medicalAccess

import com.edoctor.data.entity.remote.model.user.DoctorModel
import com.edoctor.data.entity.remote.model.user.PatientModel

data class MedicalAccessesForDoctorModel(
        val medicalAccesses: List<MedicalAccessForDoctorModel>
)

data class MedicalAccessesForPatientModel(
        val medicalAccesses: List<MedicalAccessForPatientModel>
)

data class MedicalAccessForDoctorModel(
        val patient: PatientModel,
        val medicalRecordTypes: List<MedicalRecordType>
)

data class MedicalAccessForPatientModel(
        val doctor: DoctorModel,
        val medicalRecordTypes: List<MedicalRecordType>
)

data class MedicalRecordType(
        val medicalRecordType: Int,
        val customModelName: String?,
        val customModelUnit: String?
)