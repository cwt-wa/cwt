package com.cwtsite.cwt.domain.message.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject

@DataTransferObject
data class TwitchMessageDto(
    val body: String,
    val displayName: String,
    val channelName: String
)

