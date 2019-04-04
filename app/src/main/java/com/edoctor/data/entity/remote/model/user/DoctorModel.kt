package com.edoctor.data.entity.remote.model.user

import com.edoctor.data.entity.remote.model.user.UserModel

data class DoctorModel(
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
) : UserModel()