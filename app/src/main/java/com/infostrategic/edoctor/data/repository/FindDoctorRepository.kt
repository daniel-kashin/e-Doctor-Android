package com.infostrategic.edoctor.data.repository

import com.infostrategic.edoctor.data.entity.remote.model.user.DoctorModel
import com.infostrategic.edoctor.data.mapper.UserMapper.withAbsoluteUrl
import com.infostrategic.edoctor.data.remote.rest.FindDoctorRestApi
import io.reactivex.Single

class FindDoctorRepository(
    private val api: FindDoctorRestApi
) {

    fun findDoctors(textToSearch: String): Single<List<DoctorModel>> {
        return api.getDoctors(textToSearch).map {
            it.doctors.map { withAbsoluteUrl(it) }
        }
    }

}