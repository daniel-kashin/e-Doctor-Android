package com.edoctor.data.repository

import com.edoctor.data.entity.remote.response.DoctorResponse
import com.edoctor.data.remote.api.FindDoctorRestApi
import io.reactivex.Single

class FindDoctorRepository(
    private val api: FindDoctorRestApi
) {

    fun findDoctors(textToSearch: String): Single<List<DoctorResponse>> {
        return api.getDoctors(textToSearch).map { it.doctors }
    }

}