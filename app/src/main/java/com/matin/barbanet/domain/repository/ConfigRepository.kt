package com.matin.barbanet.domain.repository

import com.matin.common.dto.LocationRequest
import com.matin.common.dto.response.AppFeaturesResponseDto
import com.matin.common.dto.response.CurrentOrderResponseDto
import com.matin.common.dto.response.UpdateAppResponseDto
import com.matin.common.dto.response.WebViewResponseDto
import com.matin.common.network.notification.SendFcmTokenOutput

interface ConfigRepository {
    suspend fun getAppVersion(): UpdateAppResponseDto
    suspend fun getAppFeatures(): List<AppFeaturesResponseDto>
    suspend fun getWebView(packageName: String): WebViewResponseDto
    suspend fun saveFcmToken(
        model: SendFcmTokenOutput?
    )
    suspend fun sendLocation(locationRequest: LocationRequest)
    suspend fun getCurrentOrder(): CurrentOrderResponseDto

}