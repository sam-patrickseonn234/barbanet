package com.matin.barbanet.domain.state

import com.matin.barbanet.domain.model.WebViewResponse

data class WebViewState(
    val isLoading: Boolean = false,
    val webView: WebViewResponse? = null,
    val error: String = ""
)