package com.cwtsite.cwt.domain.notification.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject

@DataTransferObject
data class NotificationUpdateDto(
    val subscription: Map<String, Any>?,
    val setting: List<NotificationTypeDto>?,
    val userAgent: String?,
)
