package com.edoctor.data.repository

import com.edoctor.data.entity.presentation.BodyParameterType
import com.edoctor.data.entity.presentation.LatestBodyParametersInfo
import com.edoctor.data.entity.remote.model.record.*
import com.edoctor.data.entity.presentation.BodyParameterType.*
import com.edoctor.data.entity.presentation.BodyParameterType.Custom.Companion.NEW
import com.edoctor.data.entity.presentation.LatestBodyParametersInfo.Companion.NON_CUSTOM_TYPES
import com.edoctor.data.entity.presentation.MedicalEventsInfo
import com.edoctor.data.mapper.BodyParameterMapper
import com.edoctor.data.mapper.BodyParameterMapper.fromWrapperModel
import com.edoctor.data.mapper.MedicalEventMapper
import com.edoctor.data.remote.rest.MedicalEventsRestApi
import com.edoctor.data.remote.rest.ParametersRestApi
import io.reactivex.Completable
import io.reactivex.Single

class MedicalRecordsRepository(
    private val parametersApi: ParametersRestApi,
    private val medicalEventsApi: MedicalEventsRestApi
) {

    // region events

    fun getMedicalEvents(): Single<MedicalEventsInfo> =
        medicalEventsApi
            .getEvents()
            .map {
                it.medicalEvents.mapNotNull { wrapper -> MedicalEventMapper.fromWrapper(wrapper) }
            }
            .map {
                MedicalEventsInfo(it, MedicalEventsInfo.ALL_MEDICAL_EVENTS_TYPES)
            }

    fun addOrEditEvent(event: MedicalEventModel): Single<MedicalEventModel> =
        Single
            .defer {
                medicalEventsApi.addOrEditMedicalEvent(MedicalEventMapper.toWrapper(event))
            }
            .map { MedicalEventMapper.fromWrapper(it) }

    fun removeEvent(event: MedicalEventModel): Completable =
        Completable
            .defer {
                medicalEventsApi.deleteMedicalEvent(MedicalEventMapper.toWrapper(event))
            }

    // endregion

    // region body parameters

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
                parametersApi.getParameters(BodyParameterMapper.toWrapperType(bodyParameterType))
            }
            .map {
                it.bodyParameters.mapNotNull { wrapper -> BodyParameterMapper.fromWrapperModel(wrapper) }
            }

    fun addOrEditParameter(parameter: BodyParameterModel): Single<BodyParameterModel> =
        Single
            .defer {
                parametersApi.addOrEditParameter(BodyParameterMapper.toWrapperModel(parameter))
            }
            .map { BodyParameterMapper.fromWrapperModel(it) }

    fun removeParameter(parameter: BodyParameterModel): Completable =
        Completable
            .defer {
                parametersApi.deleteParameter(BodyParameterMapper.toWrapperModel(parameter))
            }

    // endregion

}