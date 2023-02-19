package com.matin.barbanet.domain.useCase

import com.matin.barbanet.domain.repository.ConfigRepository
import com.matin.barbanet.utiles.ResultWrapper
import com.matin.common.dto.LocationRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class SendLocationUseCase @Inject constructor(
    private val configRepository: ConfigRepository
) {
    operator fun invoke(locationRequest: LocationRequest): Flow<Unit> = flow {
        try {
            configRepository.sendLocation(locationRequest)
        } catch (e: HttpException) {

        }

    }
}