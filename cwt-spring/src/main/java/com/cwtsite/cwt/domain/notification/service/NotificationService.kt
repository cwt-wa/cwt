package com.cwtsite.cwt.domain.notification.service

import com.cwtsite.cwt.domain.notification.entity.Notification
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class NotificationService @Autowired
constructor(
    private val notificationRepository: NotificationRepository,
) {
    fun findForUser(user: User): Notification? = notificationRepository.findByUser(user)

    fun save(notification: Notification): Notification = notificationRepository.save(notification)
}
