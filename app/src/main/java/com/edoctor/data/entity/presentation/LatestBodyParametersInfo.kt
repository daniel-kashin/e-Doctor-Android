package com.edoctor.data.entity.presentation

import com.edoctor.data.entity.presentation.BodyParameterType.Companion.NON_CUSTOM_BODY_PARAMETER_TYPES
import com.edoctor.data.entity.remote.model.record.BodyParameterModel

class LatestBodyParametersInfo(
    val latestBodyParametersOfEachType: List<BodyParameterModel>,
    val availableBodyParametesTypes: List<BodyParameterType>
) {
    companion object {
        val EMPTY = LatestBodyParametersInfo(
            emptyList(),
            NON_CUSTOM_BODY_PARAMETER_TYPES
        )
    }
}