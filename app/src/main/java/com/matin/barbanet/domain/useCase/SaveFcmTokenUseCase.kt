package com.matin.barbanet.domain.useCase

import com.matin.barbanet.domain.repository.ConfigRepository
import com.matin.barbanet.utiles.ResultWrapper
import com.matin.common.network.notification.SendFcmTokenOutput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject


class SaveFcmTokenUseCase @Inject constructor(
    private val configRepository: ConfigRepository
) {
    operator fun invoke(sendFcmTokenOutput: SendFcmTokenOutput): Flow<String?> = flow {
        try {
            configRepository.saveFcmToken(sendFcmTokenOutput)
        } catch (e: HttpException) {
        }
    }
}