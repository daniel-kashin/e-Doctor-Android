package com.infostrategic.edoctor.data.entity.remote.response

import com.infostrategic.edoctor.data.entity.remote.model.record.BodyParameterWrapper

data class BodyParametersResponse(
    val bodyParameters: List<BodyParameterWrapper>
)