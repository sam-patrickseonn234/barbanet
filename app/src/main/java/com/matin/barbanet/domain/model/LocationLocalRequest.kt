package com.matin.barbanet.domain.model

import com.matin.common.dto.LocationRequest

data class LocationLocalRequest(
    val accuracy: Float? = null,
    val bearing: Float? = null,
    val dateTime: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val night: Boolean? = null,
    val orderId: Int? = null,
    val speed: Float? = null,
    val weather: Int? = null
)

fun LocationRequest.toLocalRequest(): LocationLocalRequest {
    return LocationLocalRequest(
        accuracy,
        bearing,
        dateTime,
        latitude,
        longitude,
        night,
        orderId,
        speed,
        weather
    )
}
