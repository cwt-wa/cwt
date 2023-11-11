package com.cwtsite.cwt.domain.notification.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject

@DataTransferObject
data class NotificationWriteDto(
    val subscription: String?,
    val setting: Int?,
    val userAgent: String?,
)
