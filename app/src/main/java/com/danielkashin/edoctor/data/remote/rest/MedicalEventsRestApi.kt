package com.danielkashin.edoctor.data.remote.rest

import com.danielkashin.edoctor.data.entity.remote.model.record.SynchronizeEventsModel
import com.danielkashin.edoctor.data.entity.remote.response.MedicalEventsResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MedicalEventsRestApi {

    @GET("/medicalEventsForDoctor")
    fun getEventsForDoctor(
        @Query("patientUuid") patientUuid: String
    ): Single<MedicalEventsResponse>

    @POST("/synchronizeEventsForPatient")
    fun synchronizeEventsForPatient(
        @Body request: SynchronizeEventsModel
    ): Single<SynchronizeEventsModel>


}