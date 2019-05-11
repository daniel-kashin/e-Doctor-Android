package com.edoctor.data.remote.rest

import com.edoctor.data.entity.remote.model.record.SynchronizeBodyParametersModel
import com.edoctor.data.entity.remote.request.BodyParameterTypeWrapper
import com.edoctor.data.entity.remote.response.BodyParametersResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ParametersRestApi {

    @POST("/parametersForDoctor")
    fun getParametersForDoctor(
        @Body type: BodyParameterTypeWrapper,
        @Query("patientUuid") patientUuid: String
    ) : Single<BodyParametersResponse>

    @GET("/latestParametersForDoctor")
    fun getLatestParametersOfEachTypeForDoctor(
        @Query("patientUuid") patientUuid: String
    ): Single<BodyParametersResponse>

    @POST("/synchronizeParametersForPatient")
    fun synchronizeParametersForPatient(
        @Body request: SynchronizeBodyParametersModel
    ): Single<SynchronizeBodyParametersModel>

}