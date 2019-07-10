package com.danielkashin.edoctor.data.remote.rest

import com.danielkashin.edoctor.data.entity.remote.model.record.MedicalEventWrapper
import com.danielkashin.edoctor.data.entity.remote.response.MedicalEventsResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RequestedEventsRestApi {

    @GET("/requestedMedicalEventsForDoctor")
    fun getRequestedEventsForDoctor(
        @Query("patientUuid") patientUuid: String
    ): Single<MedicalEventsResponse>

    @POST("/addMedicalEventForDoctor")
    fun addMedicalEventForDoctor(
        @Body event: MedicalEventWrapper,
        @Query("patientUuid") patientUuid: String
    ): Single<MedicalEventWrapper>

}