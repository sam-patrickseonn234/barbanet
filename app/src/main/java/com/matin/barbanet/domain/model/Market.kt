package com.matin.barbanet.domain.model

import com.matin.common.dto.response.MarketDto


data class Market(
    val id: String,
    val title: String,
    val name: String
)

fun MarketDto.toMarket(): Market {
    return Market(
        id = id,
        title = title,
        name = name
    )
}
