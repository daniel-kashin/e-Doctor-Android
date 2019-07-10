package com.infostrategic.edoctor.data.repository

import com.infostrategic.edoctor.data.Preferences
import com.infostrategic.edoctor.data.entity.presentation.BodyParameterType
import com.infostrategic.edoctor.data.entity.presentation.LatestBodyParametersInfo
import com.infostrategic.edoctor.data.entity.remote.model.record.*
import com.infostrategic.edoctor.data.entity.presentation.BodyParameterType.*
import com.infostrategic.edoctor.data.entity.presentation.BodyParameterType.Companion.NON_CUSTOM_BODY_PARAMETER_TYPES
import com.infostrategic.edoctor.data.entity.presentation.BodyParameterType.Custom.Companion.NEW
import com.infostrategic.edoctor.data.entity.presentation.MedicalEventType.Companion.ALL_MEDICAL_EVENT_TYPES
import com.infostrategic.edoctor.data.entity.presentation.MedicalEventsInfo
import com.infostrategic.edoctor.data.local.event.MedicalEventLocalStore
import com.infostrategic.edoctor.data.local.parameter.BodyParameterLocalStore
import com.infostrategic.edoctor.data.mapper.BodyParameterMapper
import com.infostrategic.edoctor.data.mapper.BodyParameterMapper.toLocalFromWrapper
import com.infostrategic.edoctor.data.mapper.BodyParameterMapper.toModelFromWrapper
import com.infostrategic.edoctor.data.mapper.BodyParameterMapper.toWrapperFromLocal
import com.infostrategic.edoctor.data.mapper.MedicalEventMapper
import com.infostrategic.edoctor.data.remote.rest.MedicalEventsRestApi
import com.infostrategic.edoctor.data.remote.rest.ParametersRestApi
import com.infostrategic.edoctor.data.remote.rest.RequestedEventsRestApi
import io.reactivex.Completable
import io.reactivex.Single

class MedicalRecordsRepository(
    private val parametersApi: ParametersRestApi,
    private val medicalEventsApi: MedicalEventsRestApi,
    private val requestedEventsRestApi: RequestedEventsRestApi,
    private val bodyParameterLocalStore: BodyParameterLocalStore,
    private val medicalEventLocalStore: MedicalEventLocalStore
) {

    private val synchronizeParametersLock = Any()
    private val synchronizeEventsLock = Any()

    // region doctor

    fun getRequestedMedicalEventsForDoctor(patientUuid: String): Single<MedicalEventsInfo> =
        requestedEventsRestApi.getRequestedEventsForDoctor(patientUuid)
            .map { result ->
                result.medicalEvents
                    .mapNotNull { MedicalEventMapper.toModelFromWrapper(it) }
                    .sortedBy { it.timestamp }
            }
            .map {
                MedicalEventsInfo(it, ALL_MEDICAL_EVENT_TYPES, true)
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

    fun getMedicalEventsForDoctor(patientUuid: String): Single<MedicalEventsInfo> =
        medicalEventsApi.getEventsForDoctor(patientUuid)
            .map { result ->
                result.medicalEvents
                    .mapNotNull { MedicalEventMapper.toModelFromWrapper(it) }
                    .sortedBy { it.timestamp }
            }
            .map {
                MedicalEventsInfo(it, emptyList(), true)
            }

    fun getLatestBodyParametersInfoForDoctor(patientUuid: String): Single<LatestBodyParametersInfo> =
        parametersApi
            .getLatestParametersOfEachTypeForDoctor(patientUuid)
            .map { parameters ->
                parameters.bodyParameters
                    .mapNotNull { wrapper -> toModelFromWrapper(wrapper) }
                    .sortedBy { it.timestamp }
            }
            .map {
                LatestBodyParametersInfo(it, emptyList(), false)
            }

    fun getAllParametersOfTypeForDoctor(
        bodyParameterType: BodyParameterType,
        patientUuid: String
    ): Single<List<BodyParameterModel>> =
        Single
            .defer {
                parametersApi.getParametersForDoctor(BodyParameterMapper.toWrapperType(bodyParameterType), patientUuid)
            }
            .map { result ->
                result.bodyParameters
                    .mapNotNull { wrapper -> BodyParameterMapper.toModelFromWrapper(wrapper) }
                    .sortedBy { it.timestamp }
            }

    // endregion

    // region patient

    fun getRequestedMedicalEventsForPatient(doctorUuid: String, patientUuid: String): Single<MedicalEventsInfo> =
        synchronizeMedicalEvents(patientUuid)
            .flatMap { isSynchronized ->
                medicalEventLocalStore.getRequestedEventsForPatient(doctorUuid, patientUuid)
                    .map { localEvents ->
                        val mappedEvents = localEvents
                            .mapNotNull {
                                MedicalEventMapper.toModelFromWrapper(MedicalEventMapper.toWrapperFromLocal(it))
                            }
                            .sortedBy { it.timestamp }

                        MedicalEventsInfo(mappedEvents, emptyList(), isSynchronized)
                    }
            }

    fun getMedicalEventsForPatient(patientUuid: String): Single<MedicalEventsInfo> =
        synchronizeMedicalEvents(patientUuid)
            .flatMap { isSynchronized ->
                medicalEventLocalStore.getEventsForPatient(patientUuid)
                    .map { localEvents ->
                        val mappedEvents = localEvents
                            .mapNotNull {
                                MedicalEventMapper.toModelFromWrapper(MedicalEventMapper.toWrapperFromLocal(it))
                            }
                            .sortedBy { it.timestamp }

                        MedicalEventsInfo(mappedEvents, ALL_MEDICAL_EVENT_TYPES, isSynchronized)
                    }
            }

    fun addOrEditEventForPatient(event: MedicalEventModel, patientUuid: String): Single<MedicalEventModel> =
        medicalEventLocalStore.save(
            MedicalEventMapper.toLocalFromWrapper(
                MedicalEventMapper.toWrapperFromModel(event),
                patientUuid,
                true
            )
        ).map {
            MedicalEventMapper.toModelFromWrapper(MedicalEventMapper.toWrapperFromLocal(it))
        }


    fun deleteEventForPatient(event: MedicalEventModel): Completable =
        medicalEventLocalStore.markAsDeleted(event.uuid)

    fun getLatestBodyParametersInfoForPatient(patientUuid: String): Single<LatestBodyParametersInfo> =
        synchronizeBodyParameters(patientUuid)
            .flatMap { isSynchronized ->
                bodyParameterLocalStore.getLatestParametersOfEachTypeForPatient(patientUuid)
                    .map { result ->
                        result
                            .mapNotNull {
                                BodyParameterMapper.toModelFromWrapper(BodyParameterMapper.toWrapperFromLocal(it))
                            }
                            .sortedBy { it.timestamp }
                    }
                    .map { it to isSynchronized }
            }
            .map { (latestParameters, isSynchronized) ->
                val customTypes = latestParameters
                    .filterIsInstance<CustomBodyParameterModel>()
                    .map { Custom(it.name, it.unit) }
                    .distinct()
                    .toMutableList()

                val availableTypes = customTypes + NON_CUSTOM_BODY_PARAMETER_TYPES + NEW

                LatestBodyParametersInfo(latestParameters, availableTypes, isSynchronized)
            }

    fun getAllParametersOfTypeForPatient(
        bodyParameterType: BodyParameterType,
        patientUuid: String
    ): Single<List<BodyParameterModel>> =
        bodyParameterLocalStore
            .getParametersForPatient(
                patientUuid,
                BodyParameterMapper.toEntityType(bodyParameterType)
            )
            .map { parameters ->
                parameters
                    .mapNotNull {
                        BodyParameterMapper.toModelFromWrapper(BodyParameterMapper.toWrapperFromLocal(it))
                    }
                    .sortedBy { it.timestamp }
            }

    fun addOrEditParameterForPatient(parameter: BodyParameterModel, patientUuid: String): Single<BodyParameterModel> =
        bodyParameterLocalStore
            .save(
                BodyParameterMapper.toLocalFromWrapper(
                    BodyParameterMapper.toWrapperFromModel(parameter),
                    patientUuid,
                    true
                )
            )
            .map { BodyParameterMapper.toModelFromWrapper(BodyParameterMapper.toWrapperFromLocal(it)) }

    fun deleteParameterForPatient(parameter: BodyParameterModel): Completable =
        bodyParameterLocalStore.markAsDeleted(parameter.uuid)

    // endregion

    // region synchronize

    private fun synchronizeBodyParameters(patientUuid: String): Single<Boolean> =
        synchronized(synchronizeParametersLock) {
            Single
                .defer {
                    val lastSynchronizeTimestamp = Preferences.lastSynchronizeParametersTimestamp ?: -1

                    bodyParameterLocalStore
                        .getParametersToSynchronizeForPatient(patientUuid)
                        .map { it to lastSynchronizeTimestamp }
                }
                .flatMap { (parametersToSynchronize, lastSynchronizeTimestamp) ->
                    parametersApi.synchronizeParametersForPatient(
                        SynchronizeBodyParametersModel(
                            parametersToSynchronize.map { toWrapperFromLocal(it) },
                            lastSynchronizeTimestamp
                        )
                    )
                }
                .doOnSuccess { synchronizeBodyParametersModel ->
                    bodyParameterLocalStore
                        .saveBlocking(
                            synchronizeBodyParametersModel.bodyParameters.map {
                                toLocalFromWrapper(
                                    it,
                                    patientUuid,
                                    false
                                )
                            }
                        )
                    Preferences.lastSynchronizeParametersTimestamp = synchronizeBodyParametersModel.synchronizeTimestamp
                }
                .map { true }
                .onErrorReturnItem(false)
        }

    private fun synchronizeMedicalEvents(patientUuid: String): Single<Boolean> = synchronized(synchronizeEventsLock) {
        Single
            .defer {
                val lastSynchronizeTimestamp = Preferences.lastSynchronizeEventsTimestamp ?: -1

                medicalEventLocalStore
                    .getEventsToSynchronizeForPatient(patientUuid)
                    .map { it to lastSynchronizeTimestamp }
            }
            .flatMap { (eventsToSynchronize, lastSynchronizeTimestamp) ->
                medicalEventsApi.synchronizeEventsForPatient(
                    SynchronizeEventsModel(
                        eventsToSynchronize.map { MedicalEventMapper.toWrapperFromLocal(it) },
                        lastSynchronizeTimestamp
                    )
                )
            }
            .doOnSuccess { synchronizeEventsModel ->
                medicalEventLocalStore
                    .saveBlocking(
                        synchronizeEventsModel.events.map {
                            MedicalEventMapper.toLocalFromWrapper(
                                it,
                                patientUuid,
                                false
                            )
                        }
                    )
                Preferences.lastSynchronizeEventsTimestamp = synchronizeEventsModel.synchronizeTimestamp
            }
            .map { true }
            .onErrorReturnItem(false)
    }

    // endregion

}