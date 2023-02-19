package com.matin.barbanet.domain.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    fun getLastLocation(interval: Long): Flow<Location>
    class LocationException(message: String): Exception()
}