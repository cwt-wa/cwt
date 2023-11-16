package com.cwtsite.cwt.domain.notification.service

import com.cwtsite.cwt.domain.notification.entity.Notification
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class NotificationService @Autowired
constructor(
    private val notificationRepository: NotificationRepository,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun findSubscriptionForUser(
        user: User,
        sub: String?
    ): Notification? =
        notificationRepository.findByUser(user)
            .firstOrNull {
                runCatching {
                    JSONObject(it.subscription).getString("endpoint") == sub
                }.getOrElse {
                    logger.error("Couldn't get subscription endpoint:", it)
                    false
                }
            }

    fun save(notification: Notification): Notification = notificationRepository.save(notification)
}
