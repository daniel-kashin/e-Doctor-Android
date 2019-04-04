package com.edoctor.data.entity.presentation

import com.edoctor.data.entity.remote.model.user.UserModel
import com.stfalcon.chatkit.commons.models.IUser

fun UserModel.toPresentation() = object : IUser {

    override fun getAvatar(): String? = relativeImageUrl

    override fun getName(): String = fullName ?: email

    override fun getId(): String = email

}