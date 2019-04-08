package com.edoctor.data.repository

import com.edoctor.data.entity.presentation.BodyParameterType
import com.edoctor.data.entity.presentation.LatestBodyParametersInfo
import com.edoctor.data.entity.remote.model.record.*
import com.edoctor.data.entity.presentation.BodyParameterType.*
import com.edoctor.data.entity.presentation.BodyParameterType.Custom.Companion.NEW
import com.edoctor.data.entity.presentation.LatestBodyParametersInfo.Companion.NON_CUSTOM_TYPES
import com.edoctor.data.entity.presentation.MedicalEventType
import com.edoctor.data.entity.presentation.MedicalEventsInfo
import com.edoctor.data.entity.presentation.MedicalEventsInfo.Companion.ALL_MEDICAL_EVENTS_TYPES
import com.edoctor.data.mapper.BodyParameterMapper.fromWrapperModel
import com.edoctor.data.mapper.BodyParameterMapper.toWrapperModel
import com.edoctor.data.mapper.BodyParameterMapper.toWrapperType
import com.edoctor.data.remote.rest.ParametersRestApi
import com.edoctor.utils.currentUnixTime
import com.google.firebase.analytics.connector.AnalyticsConnectorImpl
import io.reactivex.Completable
import io.reactivex.Single

class MedicalRecordsRepository(
    private val parametersApi: ParametersRestApi
) {

    // region events

    fun getMedicalEvents(): Single<MedicalEventsInfo> {
        return Single.just(
            MedicalEventsInfo(
                listOf(
                    Analysis("1", currentUnixTime() - 1000, null, null, "На грипп", ""),
                    Allergy("2", currentUnixTime(), "", null, "Оса", "Краснота носа"),
                    Note("3", currentUnixTime(), "Заметка"),
                    Vaccination("4", currentUnixTime() - 100, "Городская поликлиника №14", null, null, null, "Против гриппа"),
                    Procedure("5", currentUnixTime() - 5000, null, null, null, null, "Обезвоживание"),
                    DoctorVisit("6", currentUnixTime() - 300, null, null, null, null, "Жалуюсь сильно", "Здоров", null),
                    Sickness("7", currentUnixTime(), null, null, null, "Корь")
                ),
                ALL_MEDICAL_EVENTS_TYPES
            )
        )
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

    // endregion

}