package com.edoctor.data.entity.presentation

import com.edoctor.data.entity.remote.model.record.BodyParameterModel

class LatestBodyParametersInfo(
    val latestBodyParametersOfEachType: List<BodyParameterModel>,
    val availableBodyParametesTypes: List<BodyParameterType>
) {
    companion object {
        val NON_CUSTOM_TYPES = listOf(
            BodyParameterType.Height,
            BodyParameterType.Weight,
            BodyParameterType.BloodOxygen,
            BodyParameterType.BloodSugar,
            BodyParameterType.BloodPressure,
            BodyParameterType.Temperature
        )

        val EMPTY = LatestBodyParametersInfo(
            emptyList(),
            NON_CUSTOM_TYPES
        )
    }
}