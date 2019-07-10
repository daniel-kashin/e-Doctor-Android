package com.danielkashin.edoctor.data.mapper

import com.danielkashin.edoctor.data.entity.remote.model.user.DoctorModel
import com.danielkashin.edoctor.data.entity.remote.model.user.PatientModel
import com.danielkashin.edoctor.data.entity.remote.model.user.UserModel
import com.danielkashin.edoctor.data.entity.remote.model.user.UserModelWrapper
import com.danielkashin.edoctor.data.injection.NetworkModule.Companion.getAbsoluteImageUrl

object UserMapper {

    fun withAbsoluteUrl(userModelWrapper: UserModelWrapper): UserModelWrapper? =
        when {
            userModelWrapper.doctorModel != null -> UserModelWrapper(
                doctorModel = withAbsoluteUrl(userModelWrapper.doctorModel)
            )
            userModelWrapper.patientModel != null -> UserModelWrapper(
                patientModel = withAbsoluteUrl(userModelWrapper.patientModel)
            )
            else -> null
        }

    fun withAbsoluteUrl(doctorModel: DoctorModel) =
        if (doctorModel.relativeImageUrl != null) {
            doctorModel.copy(relativeImageUrl = getAbsoluteImageUrl(doctorModel.relativeImageUrl))
        } else {
            doctorModel
        }

    fun withAbsoluteUrl(patientModel: PatientModel) =
        if (patientModel.relativeImageUrl != null) {
            patientModel.copy(relativeImageUrl = getAbsoluteImageUrl(patientModel.relativeImageUrl))
        } else {
            patientModel
        }

    fun unwrapResponse(userModelWrapper: UserModelWrapper): UserModel? =
        when {
            userModelWrapper.doctorModel != null -> userModelWrapper.doctorModel
            userModelWrapper.patientModel != null -> userModelWrapper.patientModel
            else -> null
        }

    fun wrapRequest(userModel: UserModel): UserModelWrapper =
        when (userModel) {
            is DoctorModel -> UserModelWrapper(doctorModel = userModel)
            is PatientModel -> UserModelWrapper(patientModel = userModel)
        }

}