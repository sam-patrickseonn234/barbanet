package com.matin.barbanet.data.remote

import com.matin.common.dto.LocationRequest
import com.matin.common.dto.response.AppFeaturesResponseDto
import com.matin.common.dto.response.CurrentOrderResponseDto
import com.matin.common.dto.response.UpdateAppResponseDto
import com.matin.common.dto.response.WebViewResponseDto
import com.matin.common.network.notification.SendFcmTokenOutput
import retrofit2.http.*

interface ConfigApi {
    @GET("rest/pub/appVersion/latest")
    suspend fun getAppVersion(): UpdateAppResponseDto

    @GET("rest/pub/appVersion/features")
    suspend fun getAppFeatures(): List<AppFeaturesResponseDto>

    @GET("rest/pub/webView/get")
    suspend fun getWebView(@Query("packageName") packageName: String): WebViewResponseDto

    @PUT("rest/idt/user/session/notification")
    suspend fun saveFcmToken(
        @Body model: SendFcmTokenOutput?
    )

    @POST("rest/drv/location/create")
    suspend fun sendLocation(
        @Query("ignore") ignore: Boolean,
        @Body locationRequest: LocationRequest
    )
    @GET("rest/drv/shipment/car/currentOrder")
    suspend fun getCurrentOrder(): CurrentOrderResponseDto

}