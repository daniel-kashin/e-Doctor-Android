package com.infostrategic.edoctor.data.entity.presentation

import com.infostrategic.edoctor.data.entity.remote.model.record.BodyParameterModel

class LatestBodyParametersInfo(
    val latestBodyParametersOfEachType: List<BodyParameterModel>,
    val availableBodyParametesTypes: List<BodyParameterType>,
    val isSynchronized: Boolean
)