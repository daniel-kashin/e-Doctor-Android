package com.edoctor.data.mapper

import com.edoctor.data.entity.remote.response.DoctorResponse
import com.edoctor.data.entity.remote.response.PatientResponse
import com.edoctor.data.entity.remote.response.UserResponse
import com.edoctor.data.entity.remote.response.UserResponseWrapper
import com.edoctor.data.injection.NetworkModule.Companion.EDOCTOR_HTTP_ENDPOINT

object UserMapper {

    fun unwrapResponse(userResponseWrapper: UserResponseWrapper): UserResponse =
        when {
            userResponseWrapper.doctorResponse != null -> userResponseWrapper.doctorResponse.let {
                if (it.relativeImageUrl != null) {
                    it.copy(relativeImageUrl = getAbsoluteImageUrl(it.relativeImageUrl))
                } else {
                    it
                }
            }
            userResponseWrapper.patientResponse != null -> userResponseWrapper.patientResponse.let {
                if (it.relativeImageUrl != null) {
                    it.copy(relativeImageUrl = getAbsoluteImageUrl(it.relativeImageUrl))
                } else {
                    it
                }
            }
            else -> throw IllegalStateException()
        }

    fun wrapRequest(userResponse: UserResponse): UserResponseWrapper =
        when (userResponse) {
            is DoctorResponse -> UserResponseWrapper(doctorResponse = userResponse)
            is PatientResponse -> UserResponseWrapper(patientResponse = userResponse)
            else -> throw IllegalStateException()
        }

    private fun getAbsoluteImageUrl(relativeImageUrl: String) = EDOCTOR_HTTP_ENDPOINT + relativeImageUrl

}