package com.edoctor.data.entity.remote.response

data class PatientResponse(
    override val city: String,
    override val email: String,
    override val fullName: String
): UserResponse()