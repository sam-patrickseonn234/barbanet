package com.matin.barbanet.domain.useCase

import com.matin.barbanet.domain.model.WebViewResponse
import com.matin.barbanet.domain.model.toWebViewResponse
import com.matin.barbanet.domain.repository.ConfigRepository
import com.matin.barbanet.utiles.ResultWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class GetWebViewUseCase @Inject constructor(
    private val configRepository: ConfigRepository
) {
    operator fun invoke(packageName: String): Flow<ResultWrapper<WebViewResponse>> = flow {
        try {
            val webView = configRepository.getWebView(packageName)
            emit(ResultWrapper.Success(webView.toWebViewResponse()))
        } catch (e: HttpException) {
            emit(ResultWrapper.Error(e.message()))
        }
    }
}