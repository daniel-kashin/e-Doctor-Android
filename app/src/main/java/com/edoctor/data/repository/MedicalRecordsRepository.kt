package com.edoctor.data.repository

import com.edoctor.data.entity.presentation.LatestBodyParametersInfo
import com.edoctor.data.entity.remote.model.record.*
import com.edoctor.data.entity.remote.model.record.BodyParameterType.*
import com.edoctor.data.entity.remote.model.record.BodyParameterType.Companion.NON_CUSTOM_TYPES
import com.edoctor.data.entity.remote.model.record.BodyParameterType.Custom.Companion.NEW
import com.edoctor.utils.currentUnixTime
import io.reactivex.Single

class MedicalRecordsRepository() {

    fun getLatestBodyParametersInfo(): Single<LatestBodyParametersInfo> = Single
        .just(
            listOf(
                HeightModel("111", currentUnixTime(), 178.5),
                WeightModel("222", currentUnixTime() - 1000, 70.0),
                BloodPressureModel("333", currentUnixTime() - 2000, 120, 80),
                CustomBodyParameterModel("444", currentUnixTime() - 3000, "Размер ноги", "см", 26.5)
            )
        ).map {
            val customTypes = it
                .filterIsInstance<CustomBodyParameterModel>()
                .map { Custom(it.name, it.unit) }
                .distinct()
                .toMutableList()

            val availableTypes = customTypes + NON_CUSTOM_TYPES + NEW

            LatestBodyParametersInfo(it, availableTypes)
        }

}