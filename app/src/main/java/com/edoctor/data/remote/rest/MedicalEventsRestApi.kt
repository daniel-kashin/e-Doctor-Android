package com.edoctor.data.remote.rest

import com.edoctor.data.entity.remote.model.record.MedicalEventWrapper
import com.edoctor.data.entity.remote.response.MedicalEventsResponse
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MedicalEventsRestApi {

    @GET("/medicalEvents")
    fun getEvents() : Single<MedicalEventsResponse>

    @POST("/addOrEditMedicalEvent")
    fun addOrEditMedicalEvent(
        @Body medicalEvent: MedicalEventWrapper
    ) : Single<MedicalEventWrapper>

    @POST("/deleteMedicalEvent")
    fun deleteMedicalEvent(
        @Body parameter: MedicalEventWrapper
    ) : Completable

}