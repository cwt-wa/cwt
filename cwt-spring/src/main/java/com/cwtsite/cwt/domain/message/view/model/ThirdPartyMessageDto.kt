package com.cwtsite.cwt.domain.message.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.message.service.MessageNewsType

@DataTransferObject
data class ThirdPartyMessageDto(
    val body: String,
    val displayName: String,
    val link: String,
    val newsType: MessageNewsType
)
