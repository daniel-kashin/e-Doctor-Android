package com.edoctor.data.entity.presentation

import com.stfalcon.chatkit.commons.models.IUser

fun String.toUser() = object : IUser {
    override fun getAvatar() = null
    override fun getName() = this@toUser
    override fun getId(): String = this@toUser
}