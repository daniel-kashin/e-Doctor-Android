package com.edoctor.data.entity.remote.response

abstract class UserResponse {
    abstract val email: String
    abstract val fullName: String
    abstract val city: String
}