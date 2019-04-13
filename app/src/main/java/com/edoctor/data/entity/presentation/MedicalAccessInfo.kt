package com.edoctor.data.entity.presentation

data class MedicalAccessInfo(
    val medicalAccess: MedicalAccessForPatient,
    val allTypes: List<MedicalRecordType>
)