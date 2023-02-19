package com.matin.barbanet.domain.useCase

import com.matin.barbanet.domain.model.AppFeaturesResponse
import com.matin.barbanet.domain.model.toAppFeatures
import com.matin.barbanet.domain.repository.ConfigRepository
import com.matin.barbanet.utiles.ResultWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class GetAppFeaturesUseCase @Inject constructor(
    private val configRepository: ConfigRepository
) {
    operator fun invoke(): Flow<ResultWrapper<List<AppFeaturesResponse>>> = flow {
        try {
            emit(ResultWrapper.Loading())
            val appFeatures = configRepository.getAppFeatures()
            emit(ResultWrapper.Success(appFeatures.map { it.toAppFeatures() }))
        } catch (e: HttpException) {
            emit(ResultWrapper.Error(e.message()))
        }
    }
}