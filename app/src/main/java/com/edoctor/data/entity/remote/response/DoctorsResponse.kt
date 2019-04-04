package com.edoctor.data.entity.remote.response

import com.edoctor.data.entity.remote.model.user.DoctorModel

data class DoctorsResponse(
    val doctors: List<DoctorModel>
)