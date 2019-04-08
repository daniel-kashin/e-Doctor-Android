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

    @POST("/parameters")
    fun getParameters(
        @Body type: BodyParameterTypeWrapper
    ) : Single<BodyParametersResponse>

    @GET("/latestParameters")
    fun getLatestParametersOfEachType() : Single<BodyParametersResponse>

    @POST("/addOrEditParameter")
    fun addOrEditParameter(
        @Body parameter: BodyParameterWrapper
    ) : Single<BodyParameterWrapper>

    @POST("/deleteParameter")
    fun deleteParameter(
        @Body parameter: BodyParameterWrapper
    ) : Completable

}