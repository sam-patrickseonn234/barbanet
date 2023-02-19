package com.matin.barbanet.domain.model

import com.google.gson.annotations.SerializedName
import com.matin.common.dto.response.WebViewResponseDto

data class WebViewResponse(
    val description: String? = null,
    val packageName: String? = null,
    val url: String? = null,
    val id: String? = null,
)

fun WebViewResponseDto.toWebViewResponse(): WebViewResponse {
    return WebViewResponse(
        description = description,
        packageName = packageName,
        url = url,
        id = id
    )
}
