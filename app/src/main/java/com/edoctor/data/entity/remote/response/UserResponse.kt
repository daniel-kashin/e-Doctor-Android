package com.edoctor.data.entity.remote.response

abstract class UserResponse {
    abstract val email: String
    abstract val fullName: String?
    abstract val city: String?
    abstract val dateOfBirthTimestamp: Long?
    abstract val isMale: Boolean?
    abstract val relativeImageUrl: String?
}