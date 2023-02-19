package com.matin.barbanet.domain.state

import com.matin.barbanet.domain.model.CurrentOrderResponse

data class CurrentOrderState (
    val isLoading: Boolean = false,
    val currentOrder: CurrentOrderResponse? = null,
    val error: String = ""
    )