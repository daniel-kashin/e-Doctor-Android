package com.edoctor.data.entity.remote.response

data class PatientResponse(
    override val email: String,
    override val fullName: String?,
    override val city: String?,
    override val dateOfBirthTimestamp: Long?,
    override val isMale: Boolean?
): UserResponse()