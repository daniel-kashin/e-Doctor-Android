package com.edoctor.data.remote.rest

import com.edoctor.data.entity.remote.response.DoctorsResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface FindDoctorRestApi {

    @GET("/doctors")
    fun getDoctors(
        @Query("textToSearch") textToSearch: String
    ) : Single<DoctorsResponse>

}