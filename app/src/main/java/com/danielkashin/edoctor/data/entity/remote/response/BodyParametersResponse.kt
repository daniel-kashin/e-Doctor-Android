package com.danielkashin.edoctor.data.entity.remote.response

import com.danielkashin.edoctor.data.entity.remote.model.record.BodyParameterWrapper

data class BodyParametersResponse(
    val bodyParameters: List<BodyParameterWrapper>
)