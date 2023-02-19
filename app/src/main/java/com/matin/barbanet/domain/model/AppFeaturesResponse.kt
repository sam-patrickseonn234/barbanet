package com.matin.barbanet.domain.model

import com.google.gson.annotations.SerializedName
import com.matin.common.dto.response.AppFeaturesResponseDto

data class AppFeaturesResponse(
    val code: Int? = null,
    val features: List<String>? = null,
    val version: String? = null,
)

fun AppFeaturesResponseDto.toAppFeatures()
        : AppFeaturesResponse {
    return AppFeaturesResponse(
        code = code,
        features = features,
        version = version
    )

}