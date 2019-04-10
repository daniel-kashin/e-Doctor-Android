package com.edoctor.data.repository

import com.edoctor.data.entity.remote.model.user.DoctorModel
import com.edoctor.data.mapper.UserMapper.withAbsoluteUrl
import com.edoctor.data.remote.rest.FindDoctorRestApi
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