package com.danielkashin.edoctor.data.remote.rest

import com.danielkashin.edoctor.data.entity.remote.response.DoctorsResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface FindDoctorRestApi {

    @GET("/doctors")
    fun getDoctors(
        @Query("textToSearch") textToSearch: String
    ) : Single<DoctorsResponse>

}