package com.infostrategic.edoctor.data.entity.presentation

import com.infostrategic.edoctor.data.entity.remote.model.user.UserModel
import com.stfalcon.chatkit.commons.models.IUser

fun UserModel.toPresentationFromNetwork() = object : IUser {

    override fun getAvatar(): String? = relativeImageUrl

    override fun getName(): String = fullName ?: uuid

    override fun getId(): String = uuid

}