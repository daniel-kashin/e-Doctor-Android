package com.edoctor.data.mapper

import com.edoctor.data.entity.remote.model.user.DoctorModel
import com.edoctor.data.entity.remote.model.user.PatientModel
import com.edoctor.data.entity.remote.model.user.UserModel
import com.edoctor.data.entity.remote.model.user.UserModelWrapper
import com.edoctor.data.injection.NetworkModule.Companion.EDOCTOR_HTTP_ENDPOINT

object UserMapper {

    fun unwrapResponse(userModelWrapper: UserModelWrapper): UserModel =
        when {
            userModelWrapper.doctorModel != null -> userModelWrapper.doctorModel.let {
                if (it.relativeImageUrl != null) {
                    it.copy(relativeImageUrl = getAbsoluteImageUrl(it.relativeImageUrl))
                } else {
                    it
                }
            }
            userModelWrapper.patientModel != null -> userModelWrapper.patientModel.let {
                if (it.relativeImageUrl != null) {
                    it.copy(relativeImageUrl = getAbsoluteImageUrl(it.relativeImageUrl))
                } else {
                    it
                }
            }
            else -> throw IllegalStateException()
        }

    fun wrapRequest(userModel: UserModel): UserModelWrapper =
        when (userModel) {
            is DoctorModel -> UserModelWrapper(doctorModel = userModel)
            is PatientModel -> UserModelWrapper(patientModel = userModel)
            else -> throw IllegalStateException()
        }

    private fun getAbsoluteImageUrl(relativeImageUrl: String) = EDOCTOR_HTTP_ENDPOINT + relativeImageUrl

}