package com.edoctor.data.repository

import com.edoctor.data.entity.presentation.BodyParameterType
import com.edoctor.data.entity.presentation.LatestBodyParametersInfo
import com.edoctor.data.entity.remote.model.record.*
import com.edoctor.data.entity.presentation.BodyParameterType.*
import com.edoctor.data.entity.presentation.BodyParameterType.Companion.NON_CUSTOM_TYPES
import com.edoctor.data.entity.presentation.BodyParameterType.Custom.Companion.NEW
import com.edoctor.data.mapper.BodyParameterMapper.fromWrapperModel
import com.edoctor.data.mapper.BodyParameterMapper.toWrapperModel
import com.edoctor.data.mapper.BodyParameterMapper.toWrapperType
import com.edoctor.data.remote.rest.ParametersRestApi
import io.reactivex.Completable
import io.reactivex.Single

class MedicalRecordsRepository(
    private val parametersApi: ParametersRestApi
) {

    fun getLatestBodyParametersInfo(): Single<LatestBodyParametersInfo> =
        parametersApi
            .getLatestParametersOfEachType()
            .map {
                it.bodyParameters.mapNotNull { wrapper -> fromWrapperModel(wrapper) }
            }
            .map {
                val customTypes = it
                    .filterIsInstance<CustomBodyParameterModel>()
                    .map { Custom(it.name, it.unit) }
                    .distinct()
                    .toMutableList()

                val availableTypes = customTypes + NON_CUSTOM_TYPES + NEW

                LatestBodyParametersInfo(it, availableTypes)
            }

    fun getAllParametersOfType(
        bodyParameterType: BodyParameterType
    ): Single<List<BodyParameterModel>> =
        Single
            .defer {
                parametersApi.getParameters(toWrapperType(bodyParameterType))
            }
            .map {
                it.bodyParameters.mapNotNull { wrapper -> fromWrapperModel(wrapper) }
            }

    fun addOrEditParameter(
        parameter: BodyParameterModel
    ): Single<BodyParameterModel> =
        Single
            .defer {
                parametersApi.addOrEditParameter(toWrapperModel(parameter))
            }
            .map { fromWrapperModel(it) }

    fun removeParameter(
        parameter: BodyParameterModel
    ): Completable =
        Completable
            .defer {
                parametersApi.deleteParameter(toWrapperModel(parameter))
            }

}