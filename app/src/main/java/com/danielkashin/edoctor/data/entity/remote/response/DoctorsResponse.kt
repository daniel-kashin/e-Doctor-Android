package com.danielkashin.edoctor.data.entity.remote.response

import com.danielkashin.edoctor.data.entity.remote.model.user.DoctorModel

data class DoctorsResponse(
    val doctors: List<DoctorModel>
)