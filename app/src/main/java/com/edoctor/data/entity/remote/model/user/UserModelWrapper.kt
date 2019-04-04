package com.edoctor.data.entity.remote.model.user

data class UserModelWrapper(
    val patientModel: PatientModel? = null,
    val doctorModel: DoctorModel? = null
)