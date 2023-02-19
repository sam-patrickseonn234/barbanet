package com.matin.barbanet.domain.model

import com.matin.common.dto.ElementOutDto
import com.matin.common.dto.response.CurrentOrderResponseDto

data class CurrentOrderResponse(
    val shipmentId: Int?,
    val step: ElementOut?
)

data class ElementOut(
    val id: Int,
    val title: String
)



fun CurrentOrderResponseDto.toCurrentResponse():
        CurrentOrderResponse {
    return CurrentOrderResponse(
        shipmentId = shipmentId,
        step = step?.toElementOut()
    )
}
fun ElementOutDto.toElementOut(): ElementOut {
    return ElementOut(
        id,
        title
    )
}