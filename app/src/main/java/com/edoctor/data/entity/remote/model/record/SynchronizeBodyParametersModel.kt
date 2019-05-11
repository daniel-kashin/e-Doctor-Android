package com.edoctor.data.entity.remote.model.record

data class SynchronizeBodyParametersModel(
        val bodyParameters: List<BodyParameterWrapper>,
        val synchronizeTimestamp: Long
)