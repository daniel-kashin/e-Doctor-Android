package com.edoctor.data.entity.remote.model.user

import java.io.Serializable

sealed class UserModel : Serializable {
    abstract val uuid: String
    abstract val fullName: String?
    abstract val city: String?
    abstract val dateOfBirthTimestamp: Long?
    abstract val isMale: Boolean?
    abstract val relativeImageUrl: String?
}

data class DoctorModel(
    override val uuid: String,
    override val fullName: String? = null,
    override val city: String? = null,
    override val dateOfBirthTimestamp: Long? = null,
    override val isMale: Boolean? = null,
    override val relativeImageUrl: String? = null,
    val yearsOfExperience: Int? = null,
    val category: Int? = null,
    val specialization: String? = null,
    val clinicalInterests: String? = null,
    val education: String? = null,
    val workExperience: String? = null,
    val trainings: String? = null
) : UserModel()

data class PatientModel(
    override val uuid: String,
    override val fullName: String? = null,
    override val city: String? = null,
    override val dateOfBirthTimestamp: Long? = null,
    override val isMale: Boolean? = null,
    override val relativeImageUrl: String? = null,
    val bloodGroup: Int? = null
): UserModel()