package com.matin.barbanet.presentation.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matin.barbanet.domain.model.CurrentOrderResponse
import com.matin.barbanet.domain.model.LocationLocalRequest
import com.matin.barbanet.domain.model.WebViewResponse
import com.matin.barbanet.domain.state.AppFeaturesState
import com.matin.barbanet.domain.state.AppVersionState
import com.matin.barbanet.domain.state.CurrentOrderState
import com.matin.barbanet.domain.state.WebViewState
import com.matin.barbanet.domain.useCase.*
import com.matin.barbanet.utiles.ResultWrapper
import com.matin.common.network.notification.SendFcmTokenOutput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverViewModel @Inject constructor(
    private val getAppFeaturesUseCase: GetAppFeaturesUseCase,
    private val getAppVersionUseCase: GetAppVersionUseCase,
    private val getWebViewUseCase: GetWebViewUseCase,
    private val saveFcmTokenUseCase: SaveFcmTokenUseCase,
    private val getCurrentOrderUseCase: GetCurrentOrderUseCase
) : ViewModel() {
    private val _featureState = mutableStateOf(AppFeaturesState())
    val featureState: State<AppFeaturesState> = _featureState

    private val _versionState = mutableStateOf(AppVersionState())
    val versionState: State<AppVersionState> = _versionState

    private val _webViewState = mutableStateOf(WebViewState())
    val webViewState: State<WebViewState> = _webViewState

    private val _currentOrder = mutableStateOf(CurrentOrderState())
    val currentOrder: State<CurrentOrderState> = _currentOrder

    private val _currentLocation = mutableStateOf(LocationLocalRequest())
    val currentLocation: State<LocationLocalRequest> = _currentLocation
    init {
        getAppVersionResponse()
        getAppFeatures()
        getCurrentOrder()
    }

    private fun getAppFeatures() {
        getAppFeaturesUseCase().onEach { result ->
            when (result) {
                is ResultWrapper.Success -> {
                    _featureState.value = AppFeaturesState(appFeatures = result.data ?: emptyList())
                }
                is ResultWrapper.Error -> {
                    _featureState.value =
                        AppFeaturesState(error = result.message ?: "Unknown Error")

                }
                is ResultWrapper.Loading -> {
                    _featureState.value = AppFeaturesState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getAppVersionResponse() {
        getAppVersionUseCase().onEach { result ->
            when (result) {
                is ResultWrapper.Success -> {
                    _versionState.value = AppVersionState(appVersion = result.data ?: null)
                }
                is ResultWrapper.Error -> {
                    _versionState.value = AppVersionState(error = result.message ?: "Unknown Error")
                }
                is ResultWrapper.Loading -> {
                    _versionState.value = AppVersionState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getWebView(packageName: String) {
        getWebViewUseCase(packageName).onEach { result ->
            when (result) {
                is ResultWrapper.Success -> {
                    _webViewState.value =
                        WebViewState(webView = result.data ?: null, isLoading = false)
                }
                is ResultWrapper.Error -> {
                    _webViewState.value = WebViewState(error = result.message ?: "Unknown Error")
                }
                is ResultWrapper.Loading -> {
                    _webViewState.value = WebViewState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getNotification(
        sendFcmTokenOutput: SendFcmTokenOutput
    ) {
        saveFcmTokenUseCase(
            sendFcmTokenOutput
        ).launchIn(viewModelScope)
    }

    fun getCurrentOrder() {
        getCurrentOrderUseCase().onEach { result ->
            when (result) {
                is ResultWrapper.Success -> {
                    _currentOrder.value =
                        CurrentOrderState(currentOrder = result.data ?: null, isLoading = false)
                }
                is ResultWrapper.Error -> {
                    _currentOrder.value = CurrentOrderState(error = result.message ?: "Unknown Error")
                }
                is ResultWrapper.Loading -> {
                    _currentOrder.value = CurrentOrderState(isLoading = false)
                }
            }

        }.launchIn(viewModelScope)
    }

}