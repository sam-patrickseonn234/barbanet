package com.matin.barbanet.domain.model

import com.matin.common.dto.response.MarketsDto

data class Markets(
    val deepLink: String,
    val market: Market,
    val uploaded: Boolean,
    val url: String
)
fun MarketsDto.toMarkets(): Markets {
    return Markets(
        deepLink = deepLink,
        market = market.toMarket(),
        uploaded = uploaded,
        url = url
    )
}

