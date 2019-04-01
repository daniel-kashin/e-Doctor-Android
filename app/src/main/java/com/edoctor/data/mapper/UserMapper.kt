package com.edoctor.data.mapper

import com.edoctor.data.entity.remote.response.DoctorResponse
import com.edoctor.data.entity.remote.response.PatientResponse
import com.edoctor.data.entity.remote.response.UserResponse
import com.edoctor.data.entity.remote.response.UserResponseWrapper

object UserMapper {

    fun unwrapResponse(userResponseWrapper: UserResponseWrapper): UserResponse =
        when {
            userResponseWrapper.doctorResponse != null -> userResponseWrapper.doctorResponse
            userResponseWrapper.patientResponse != null -> userResponseWrapper.patientResponse
            else -> throw IllegalStateException()
        }

    fun wrapRequest(userResponse: UserResponse): UserResponseWrapper =
        when (userResponse) {
            is DoctorResponse -> UserResponseWrapper(doctorResponse = userResponse)
            is PatientResponse -> UserResponseWrapper(patientResponse = userResponse)
            else -> throw IllegalStateException()
        }

}