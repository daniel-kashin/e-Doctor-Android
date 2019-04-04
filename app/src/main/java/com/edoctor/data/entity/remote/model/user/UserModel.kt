package com.edoctor.data.entity.remote.model.user

import java.io.Serializable

sealed class UserModel : Serializable {
    abstract val email: String
    abstract val fullName: String?
    abstract val city: String?
    abstract val dateOfBirthTimestamp: Long?
    abstract val isMale: Boolean?
    abstract val relativeImageUrl: String?
}

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

data class PatientModel(
    override val email: String,
    override val fullName: String?,
    override val city: String?,
    override val dateOfBirthTimestamp: Long?,
    override val isMale: Boolean?,
    override val relativeImageUrl: String?,
    val bloodGroup: Int?
): UserModel()