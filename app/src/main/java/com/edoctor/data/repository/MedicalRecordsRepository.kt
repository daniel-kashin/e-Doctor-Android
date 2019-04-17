package com.edoctor.data.repository

import com.edoctor.data.entity.presentation.BodyParameterType
import com.edoctor.data.entity.presentation.LatestBodyParametersInfo
import com.edoctor.data.entity.remote.model.record.*
import com.edoctor.data.entity.presentation.BodyParameterType.*
import com.edoctor.data.entity.presentation.BodyParameterType.Companion.NON_CUSTOM_BODY_PARAMETER_TYPES
import com.edoctor.data.entity.presentation.BodyParameterType.Custom.Companion.NEW
import com.edoctor.data.entity.presentation.MedicalEventType.Companion.ALL_MEDICAL_EVENT_TYPES
import com.edoctor.data.entity.presentation.MedicalEventsInfo
import com.edoctor.data.local.event.MedicalEventLocalStore
import com.edoctor.data.local.parameter.BodyParameterLocalStore
import com.edoctor.data.mapper.BodyParameterMapper
import com.edoctor.data.mapper.BodyParameterMapper.toModelFromWrapper
import com.edoctor.data.mapper.MedicalEventMapper
import com.edoctor.data.remote.rest.MedicalEventsRestApi
import com.edoctor.data.remote.rest.ParametersRestApi
import com.edoctor.data.remote.rest.RequestedEventsRestApi
import io.reactivex.Completable
import io.reactivex.Single

class MedicalRecordsRepository(
    private val parametersApi: ParametersRestApi,
    private val medicalEventsApi: MedicalEventsRestApi,
    private val requestedEventsRestApi: RequestedEventsRestApi,
    private val bodyParameterLocalStore: BodyParameterLocalStore,
    private val medicalEventLocalStore: MedicalEventLocalStore
) {

    // region requested events

    fun getRequestedMedicalEventsForPatient(doctorUuid: String): Single<MedicalEventsInfo> =
        requestedEventsRestApi.getRequestedEventsForPatient(doctorUuid)
            .map {
                it.medicalEvents.mapNotNull { wrapper -> MedicalEventMapper.toModelFromWrapper(wrapper) }
            }
            .map {
                MedicalEventsInfo(it, emptyList())
            }

    fun getRequestedMedicalEventsForDoctor(patientUuid: String): Single<MedicalEventsInfo> =
        requestedEventsRestApi.getRequestedEventsForDoctor(patientUuid)
            .map {
                it.medicalEvents.mapNotNull { wrapper -> MedicalEventMapper.toModelFromWrapper(wrapper) }
            }
            .map {
                MedicalEventsInfo(it, ALL_MEDICAL_EVENT_TYPES)
            }

    fun addMedicalEventForDoctor(event: MedicalEventModel, patientUuid: String): Single<MedicalEventModel> =
        Single
            .defer {
                requestedEventsRestApi.addMedicalEventForDoctor(MedicalEventMapper.toWrapperFromModel(event), patientUuid)
            }
            .map { MedicalEventMapper.toModelFromWrapper(it) }

    // endregion

    // region events

    fun getMedicalEventsForPatient(): Single<MedicalEventsInfo> =
        medicalEventsApi
            .getEventsForPatient()
            .map {
                it.medicalEvents.mapNotNull { wrapper -> MedicalEventMapper.toModelFromWrapper(wrapper) }
            }
            .map {
                MedicalEventsInfo(it, ALL_MEDICAL_EVENT_TYPES)
            }

    fun getMedicalEventsForDoctor(patientUuid: String): Single<MedicalEventsInfo> =
        medicalEventsApi
            .getEventsForDoctor(patientUuid)
            .map {
                it.medicalEvents.mapNotNull { wrapper -> MedicalEventMapper.toModelFromWrapper(wrapper) }
            }
            .map {
                MedicalEventsInfo(it, emptyList())
            }

    fun addOrEditEventForPatient(event: MedicalEventModel): Single<MedicalEventModel> =
        Single
            .defer {
                medicalEventsApi.addOrEditMedicalEventForPatient(MedicalEventMapper.toWrapperFromModel(event))
            }
            .map { MedicalEventMapper.toModelFromWrapper(it) }

    fun deleteEventForPatient(event: MedicalEventModel): Completable =
        Completable
            .defer {
                medicalEventsApi.deleteMedicalEventForPatient(MedicalEventMapper.toWrapperFromModel(event))
            }

    // endregion

    // region body parameters

    fun getLatestBodyParametersInfoForPatient(): Single<LatestBodyParametersInfo> =
        parametersApi
            .getLatestParametersOfEachTypeForPatient()
            .map {
                it.bodyParameters.mapNotNull { wrapper -> toModelFromWrapper(wrapper) }
            }
            .map {
                val customTypes = it
                    .filterIsInstance<CustomBodyParameterModel>()
                    .map { Custom(it.name, it.unit) }
                    .distinct()
                    .toMutableList()

                val availableTypes = customTypes + NON_CUSTOM_BODY_PARAMETER_TYPES + NEW

                LatestBodyParametersInfo(it, availableTypes)
            }

    fun getLatestBodyParametersInfoForDoctor(patientUuid: String): Single<LatestBodyParametersInfo> =
        parametersApi
            .getLatestParametersOfEachTypeForDoctor(patientUuid)
            .map {
                it.bodyParameters.mapNotNull { wrapper -> toModelFromWrapper(wrapper) }
            }
            .map {
                LatestBodyParametersInfo(it, emptyList())
            }

    fun getAllParametersOfTypeForPatient(
        bodyParameterType: BodyParameterType
    ): Single<List<BodyParameterModel>> =
        Single
            .defer {
                parametersApi.getParametersForPatient(BodyParameterMapper.toWrapperType(bodyParameterType))
            }
            .map {
                it.bodyParameters.mapNotNull { wrapper -> BodyParameterMapper.toModelFromWrapper(wrapper) }
            }

    fun getAllParametersOfTypeForDoctor(
        bodyParameterType: BodyParameterType,
        patientUuid: String
    ): Single<List<BodyParameterModel>> =
        Single
            .defer {
                parametersApi.getParametersForDoctor(BodyParameterMapper.toWrapperType(bodyParameterType), patientUuid)
            }
            .map {
                it.bodyParameters.mapNotNull { wrapper -> BodyParameterMapper.toModelFromWrapper(wrapper) }
            }

    fun addOrEditParameterPatient(parameter: BodyParameterModel): Single<BodyParameterModel> =
        Single
            .defer {
                parametersApi.addOrEditParameterForPatient(BodyParameterMapper.toWrapperFromModel(parameter))
            }
            .map { BodyParameterMapper.toModelFromWrapper(it) }

    fun removeParameterForPatient(parameter: BodyParameterModel): Completable =
        Completable
            .defer {
                parametersApi.deleteParameterForPatient(BodyParameterMapper.toWrapperFromModel(parameter))
            }

    // endregion

}