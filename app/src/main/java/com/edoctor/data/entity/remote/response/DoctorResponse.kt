package com.edoctor.data.entity.remote.response

data class DoctorResponse(
    override val email: String,
    override val city: String?,
    override val fullName: String?
) : UserResponse()