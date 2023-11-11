package com.cwtsite.cwt.domain.notification.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.notification.entity.Notification
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto

@DataTransferObject
data class NotificationViewDto(
    val user: UserMinimalDto,
    val setting: Int,
) {
    companion object {
        fun toDto(n: Notification): NotificationViewDto =
            NotificationViewDto(
                user = UserMinimalDto.toDto(n.user),
                setting = n.setting,
            )

        fun empty(user: User): NotificationViewDto =
            NotificationViewDto(
                user = UserMinimalDto.toDto(user),
                setting = 0,
            )
    }
}
