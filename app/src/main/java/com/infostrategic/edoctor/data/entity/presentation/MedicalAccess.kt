package com.infostrategic.edoctor.data.entity.presentation

import com.infostrategic.edoctor.data.entity.remote.model.user.DoctorModel
import com.infostrategic.edoctor.data.entity.remote.model.user.PatientModel
import java.io.Serializable

data class MedicalAccessesForDoctor(
    val medicalAccesses: List<MedicalAccessForDoctor>
) : Serializable

data class MedicalAccessesForPatient(
    val medicalAccesses: List<MedicalAccessForPatient>,
    val allTypes: List<MedicalRecordType>
) : Serializable

data class MedicalAccessForDoctor(
    val patient: PatientModel,
    val availableTypes: List<MedicalRecordType>,
    val allTypes: List<MedicalRecordType>
) : Serializable

data class MedicalAccessForPatient(
    val doctor: DoctorModel,
    val availableTypes: List<MedicalRecordType>
) : Serializable