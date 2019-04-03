package com.edoctor.data.entity.remote.response

data class DoctorResponse(
    override val email: String,
    override val fullName: String?,
    override val city: String?,
    override val dateOfBirthTimestamp: Long?,
    override val isMale: Boolean?,
    override val relativeImageUrl: String?,
    val yearsOfExperience: Int?,
    val category: Int?,
    val specialization: String?,
    val clinicalInterests: String?,
    val education: String?,
    val workExperience: String?,
    val trainings: String?
) : UserResponse()