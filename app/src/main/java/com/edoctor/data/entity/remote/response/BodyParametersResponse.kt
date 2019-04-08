package com.edoctor.data.entity.remote.response

import com.edoctor.data.entity.remote.model.record.BodyParameterWrapper

data class BodyParametersResponse(
    val bodyParameters: List<BodyParameterWrapper>
)