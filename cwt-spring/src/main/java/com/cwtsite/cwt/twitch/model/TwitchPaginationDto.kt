package com.cwtsite.cwt.twitch.model

import com.cwtsite.cwt.domain.core.DataTransferObject

@DataTransferObject
data class TwitchPaginationDto (
        val cursor: String
)
