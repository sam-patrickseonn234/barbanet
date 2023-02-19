package com.matin.barbanet.domain.useCase

import com.matin.barbanet.domain.model.UpdateAppResponse
import com.matin.barbanet.domain.model.toUpdateResponse
import com.matin.barbanet.domain.repository.ConfigRepository
import com.matin.barbanet.utiles.ResultWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class GetAppVersionUseCase @Inject constructor(
    private val configRepository: ConfigRepository
) {
    operator fun invoke(): Flow<ResultWrapper<UpdateAppResponse>> = flow {
        try {
            emit(ResultWrapper.Loading())
            val updateResponse = configRepository.getAppVersion()
            emit(ResultWrapper.Success(updateResponse.toUpdateResponse()))
        } catch (e: HttpException) {
            emit(ResultWrapper.Error(e.message()))
        }
    }
}