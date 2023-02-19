package com.matin.barbanet.data.repository

import com.matin.barbanet.data.remote.ConfigApi
import com.matin.barbanet.domain.repository.ConfigRepository
import com.matin.common.dto.LocationRequest
import com.matin.common.dto.response.AppFeaturesResponseDto
import com.matin.common.dto.response.CurrentOrderResponseDto
import com.matin.common.dto.response.UpdateAppResponseDto
import com.matin.common.dto.response.WebViewResponseDto
import com.matin.common.network.notification.SendFcmTokenOutput
import javax.inject.Inject

class ConfigRepositoryImpl @Inject constructor(
    private val api: ConfigApi
): ConfigRepository {
    override suspend fun getAppVersion(): UpdateAppResponseDto {
        return api.getAppVersion()
    }

    override suspend fun getAppFeatures(): List<AppFeaturesResponseDto> {
        return api.getAppFeatures()
    }

    override suspend fun getWebView(packageName: String): WebViewResponseDto {
        return api.getWebView(packageName)
    }

    override suspend fun saveFcmToken(
        model: SendFcmTokenOutput?
    ) {
        api.saveFcmToken(
            model
        )
    }

    override suspend fun sendLocation(locationRequest: LocationRequest) {
        api.sendLocation(false, locationRequest)
    }

    override suspend fun getCurrentOrder(): CurrentOrderResponseDto {
        return api.getCurrentOrder()
    }
}