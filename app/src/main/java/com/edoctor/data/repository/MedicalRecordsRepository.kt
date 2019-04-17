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

    fun getRequestedMedicalEventsForPatient(doctorUuid: String, patientUuid: String): Single<MedicalEventsInfo> =
        requestedEventsRestApi.getRequestedEventsForPatient(doctorUuid)
            .doOnSuccess { result ->
                medicalEventLocalStore.saveBlocking(
                    result.medicalEvents.map { MedicalEventMapper.toLocalFromWrapper(it, patientUuid) }
                )
            }
            .map { result ->
                MedicalEventsInfo(
                    result.medicalEvents.mapNotNull { MedicalEventMapper.toModelFromWrapper(it) },
                    emptyList()
                )
            }
            .onErrorResumeNext {
                medicalEventLocalStore.getRequestedEventsForPatient(doctorUuid, patientUuid)
                    .map { localEvents ->
                        MedicalEventsInfo(
                            localEvents.mapNotNull {
                                MedicalEventMapper.toModelFromWrapper(MedicalEventMapper.toWrapperFromLocal(it))
                            },
                            emptyList()
                        )
                    }
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
                requestedEventsRestApi.addMedicalEventForDoctor(
                    MedicalEventMapper.toWrapperFromModel(event),
                    patientUuid
                )
            }
            .map { MedicalEventMapper.toModelFromWrapper(it) }

    // endregion

    // region events

    fun getMedicalEventsForPatient(patientUuid: String): Single<MedicalEventsInfo> =
        medicalEventsApi.getEventsForPatient()
            .doOnSuccess { result ->
                medicalEventLocalStore.saveBlocking(
                    result.medicalEvents.map { MedicalEventMapper.toLocalFromWrapper(it, patientUuid) }
                )
            }
            .map { result ->
                MedicalEventsInfo(
                    result.medicalEvents.mapNotNull { MedicalEventMapper.toModelFromWrapper(it) },
                    ALL_MEDICAL_EVENT_TYPES
                )
            }
            .onErrorResumeNext {
                medicalEventLocalStore.getEventsForPatient(patientUuid)
                    .map { localEvents ->
                        MedicalEventsInfo(
                            localEvents.mapNotNull {
                                MedicalEventMapper.toModelFromWrapper(MedicalEventMapper.toWrapperFromLocal(it))
                            },
                            emptyList()
                        )
                    }
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

    fun addOrEditEventForPatient(event: MedicalEventModel, patientUuid: String): Single<MedicalEventModel> =
        Single
            .defer {
                medicalEventsApi.addOrEditMedicalEventForPatient(MedicalEventMapper.toWrapperFromModel(event))
            }
            .doOnSuccess { wrapper ->
                medicalEventLocalStore.saveBlocking(MedicalEventMapper.toLocalFromWrapper(wrapper, patientUuid))
            }
            .map { wrapper ->
                MedicalEventMapper.toModelFromWrapper(wrapper)
            }

    fun deleteEventForPatient(event: MedicalEventModel): Completable =
        Completable
            .defer {
                medicalEventsApi.deleteMedicalEventForPatient(MedicalEventMapper.toWrapperFromModel(event))
            }
            .doOnComplete {
                medicalEventLocalStore.deleteById(event.uuid)
            }

    // endregion

    // region body parameters

    fun getLatestBodyParametersInfoForPatient(patientUuid: String): Single<LatestBodyParametersInfo> =
        parametersApi
            .getLatestParametersOfEachTypeForPatient()
            .doOnSuccess { result ->
                bodyParameterLocalStore.saveBlocking(
                    result.bodyParameters.map {
                        BodyParameterMapper.toLocalFromWrapper(it, patientUuid)
                    }
                )
            }
            .map { result ->
                result.bodyParameters.mapNotNull { toModelFromWrapper(it) }
            }
            .onErrorResumeNext {
                bodyParameterLocalStore.getLatestParametersOfEachTypeForPatient(patientUuid)
                    .map { latestParameters ->
                        latestParameters.mapNotNull {
                            BodyParameterMapper.toModelFromWrapper(BodyParameterMapper.toWrapperFromLocal(it))
                        }
                    }
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
        bodyParameterType: BodyParameterType,
        patientUuid: String
    ): Single<List<BodyParameterModel>> =
        Single
            .defer {
                parametersApi.getParametersForPatient(BodyParameterMapper.toWrapperType(bodyParameterType))
            }
            .doOnSuccess { result ->
                bodyParameterLocalStore.saveBlocking(
                    result.bodyParameters.map {
                        BodyParameterMapper.toLocalFromWrapper(it, patientUuid)
                    }
                )
            }
            .map {
                it.bodyParameters.mapNotNull { wrapper -> BodyParameterMapper.toModelFromWrapper(wrapper) }
            }
            .onErrorResumeNext {
                bodyParameterLocalStore.getParametersForPatient(patientUuid, BodyParameterMapper.toEntityType(bodyParameterType))
                    .map { parameters ->
                        parameters.mapNotNull {
                            BodyParameterMapper.toModelFromWrapper(BodyParameterMapper.toWrapperFromLocal(it))
                        }
                    }
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

    fun addOrEditParameterPatient(parameter: BodyParameterModel, patientUuid: String): Single<BodyParameterModel> =
        Single
            .defer {
                parametersApi.addOrEditParameterForPatient(BodyParameterMapper.toWrapperFromModel(parameter))
            }
            .doOnSuccess {
                bodyParameterLocalStore.saveBlocking(BodyParameterMapper.toLocalFromWrapper(it, patientUuid))
            }
            .map { BodyParameterMapper.toModelFromWrapper(it) }

    fun removeParameterForPatient(parameter: BodyParameterModel): Completable =
        Completable
            .defer {
                parametersApi.deleteParameterForPatient(BodyParameterMapper.toWrapperFromModel(parameter))
            }
            .doOnComplete {
                bodyParameterLocalStore.deleteById(parameter.uuid)
            }

    // endregion

}