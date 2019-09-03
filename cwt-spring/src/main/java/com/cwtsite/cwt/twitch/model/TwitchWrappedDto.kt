package com.cwtsite.cwt.twitch.model

import com.cwtsite.cwt.domain.core.DataTransferObject

@DataTransferObject
data class TwitchWrappedDto<T>(
        val data: List<T>,
        val pagination: TwitchPaginationDto?
)
