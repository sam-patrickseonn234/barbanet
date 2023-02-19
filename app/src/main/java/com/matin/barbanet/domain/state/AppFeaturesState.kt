package com.matin.barbanet.domain.state

import com.matin.barbanet.domain.model.AppFeaturesResponse

data class AppFeaturesState(
    val isLoading: Boolean = false,
    val appFeatures: List<AppFeaturesResponse> = emptyList(),
    val error: String = ""
)