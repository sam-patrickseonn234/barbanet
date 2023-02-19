package com.matin.barbanet.domain.state

import com.matin.barbanet.domain.model.UpdateAppResponse

data class AppVersionState(
    val isLoading: Boolean = false,
    val appVersion: UpdateAppResponse? = null,
    val error: String = ""
)
