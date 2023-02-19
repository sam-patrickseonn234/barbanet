package com.matin.barbanet.domain.useCase

import com.matin.barbanet.domain.model.CurrentOrderResponse
import com.matin.barbanet.domain.model.toCurrentResponse
import com.matin.barbanet.domain.repository.ConfigRepository
import com.matin.barbanet.utiles.ResultWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class GetCurrentOrderUseCase @Inject constructor(
    private val configRepository: ConfigRepository
) {
    operator fun invoke(): Flow<ResultWrapper<CurrentOrderResponse>> = flow {
        try {
            emit(ResultWrapper.Loading())
            val orderResponse = configRepository.getCurrentOrder()
            emit(ResultWrapper.Success(orderResponse.toCurrentResponse()))
        } catch (e: HttpException) {
            emit(ResultWrapper.Error(e.message()))
        }
    }
}