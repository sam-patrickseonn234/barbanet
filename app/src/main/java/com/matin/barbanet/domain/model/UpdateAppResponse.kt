package com.matin.barbanet.domain.model

import com.matin.common.dto.response.UpdateAppResponseDto

data class UpdateAppResponse(
    val code: Int? = null,
    val force: Boolean? = false,
    val file: String,
    val markets: List<Markets>,
    val url: String?,
    val version: String
)

fun UpdateAppResponseDto.toUpdateResponse(): UpdateAppResponse {
    return UpdateAppResponse(
        code = code,
        force = force,
        file = file,
        markets = markets.map { it.toMarkets() },
        url = url,
        version = version
    )
}

