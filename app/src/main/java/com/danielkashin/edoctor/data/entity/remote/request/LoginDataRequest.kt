package com.danielkashin.edoctor.data.entity.remote.request

data class LoginDataRequest(
    val email: String,
    val password: String,
    val isPatient: Boolean
)