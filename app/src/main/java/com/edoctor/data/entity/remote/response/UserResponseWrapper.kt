package com.edoctor.data.entity.remote.response

data class UserResponseWrapper(
    val patientResponse: PatientResponse? = null,
    val doctorResponse: DoctorResponse? = null
)