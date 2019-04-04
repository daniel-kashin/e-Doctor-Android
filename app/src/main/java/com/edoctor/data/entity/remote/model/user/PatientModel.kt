package com.edoctor.data.entity.remote.model.user

data class PatientModel(
    override val email: String,
    override val fullName: String?,
    override val city: String?,
    override val dateOfBirthTimestamp: Long?,
    override val isMale: Boolean?,
    override val relativeImageUrl: String?,
    val bloodGroup: Int?
): UserModel()