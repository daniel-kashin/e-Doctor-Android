package com.edoctor.data.entity.presentation

import com.edoctor.data.entity.remote.model.record.BodyParameterModel

class LatestBodyParametersInfo(
    val latestBodyParametersOfEachType: List<BodyParameterModel>,
    val availableBodyParametesTypes: List<BodyParameterType>
)