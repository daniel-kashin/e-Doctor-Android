package com.edoctor.data.remote.rest

import com.edoctor.data.entity.remote.model.record.MedicalEventWrapper
import com.edoctor.data.entity.remote.response.MedicalEventsResponse
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MedicalEventsRestApi {

    @GET("/medicalEventsForPatient")
    fun getEventsForPatient() : Single<MedicalEventsResponse>

    @POST("/addOrEditMedicalEventForPatient")
    fun addOrEditMedicalEventForPatient(
        @Body medicalEvent: MedicalEventWrapper
    ) : Single<MedicalEventWrapper>

    @POST("/deleteMedicalEventForPatient")
    fun deleteMedicalEventForPatient(
        @Body parameter: MedicalEventWrapper
    ) : Completable

}