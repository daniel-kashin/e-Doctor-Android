package com.edoctor.data.entity.presentation

import com.edoctor.data.entity.remote.model.user.DoctorModel
import com.edoctor.data.entity.remote.model.user.PatientModel

data class MedicalAccessesForDoctor(
    val medicalAccesses: List<MedicalAccessForDoctor>
)

data class MedicalAccessesForPatient(
    val medicalAccesses: List<MedicalAccessForPatient>,
    val allTypes: List<MedicalRecordType>
)

data class MedicalAccessForDoctor(
    val patient: PatientModel,
    val availableTypes: List<MedicalRecordType>
)

data class MedicalAccessForPatient(
    val doctor: DoctorModel,
    val availableTypes: List<MedicalRecordType>
)