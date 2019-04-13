package com.edoctor.data.remote.rest

import com.edoctor.data.entity.remote.model.record.BodyParameterWrapper
import com.edoctor.data.entity.remote.request.BodyParameterTypeWrapper
import com.edoctor.data.entity.remote.response.BodyParametersResponse
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ParametersRestApi {

    @POST("/parametersForPatient")
    fun getParametersForPatient(
        @Body type: BodyParameterTypeWrapper
    ) : Single<BodyParametersResponse>

    @GET("/latestParametersForPatient")
    fun getLatestParametersOfEachTypeForPatient() : Single<BodyParametersResponse>

    @POST("/addOrEditParameterForPatient")
    fun addOrEditParameterForPatient(
        @Body parameter: BodyParameterWrapper
    ) : Single<BodyParameterWrapper>

    @POST("/deleteParameterForPatient")
    fun deleteParameterForPatient(
        @Body parameter: BodyParameterWrapper
    ) : Completable

}