package com.cwtsite.cwt.domain.notification.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.fasterxml.jackson.databind.JsonNode

@DataTransferObject
data class NotificationWriteDto(
    val subscription: JsonNode,
    val setting: List<NotificationTypeDto>? = null,
    val userAgent: String? = null,
)
