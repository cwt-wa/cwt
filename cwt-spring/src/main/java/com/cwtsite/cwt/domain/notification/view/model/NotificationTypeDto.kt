package com.cwtsite.cwt.domain.notification.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.notification.NotificationType

@DataTransferObject
data class NotificationTypeDto(
    val name: String,
    val pos: Int,
    val label: String,
    val on: Boolean,
) {
    companion object {
        fun toDto(t: NotificationType, setting: Int) =
            NotificationTypeDto(
                name = t.name,
                pos = t.pos,
                label = t.label,
                on = t.on(setting),
            )

        fun fromDtos(dtos: List<NotificationTypeDto>): Int =
            NotificationType.fromSetting(dtos.map { it.pos }, dtos.map { it.on })
    }
}