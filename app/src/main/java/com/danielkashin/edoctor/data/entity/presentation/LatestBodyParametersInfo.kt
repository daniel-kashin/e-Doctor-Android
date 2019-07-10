package com.danielkashin.edoctor.data.entity.presentation

import com.danielkashin.edoctor.data.entity.remote.model.record.BodyParameterModel

class LatestBodyParametersInfo(
    val latestBodyParametersOfEachType: List<BodyParameterModel>,
    val availableBodyParametesTypes: List<BodyParameterType>,
    val isSynchronized: Boolean
)