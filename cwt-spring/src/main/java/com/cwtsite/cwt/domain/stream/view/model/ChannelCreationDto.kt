package com.cwtsite.cwt.domain.stream.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject

@DataTransferObject
data class  ChannelCreationDto(
        val twitchLoginName: String,
        val user: Long
)
