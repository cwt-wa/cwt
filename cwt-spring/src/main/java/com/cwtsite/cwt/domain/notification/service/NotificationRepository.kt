package com.cwtsite.cwt.domain.notification.service

import com.cwtsite.cwt.domain.notification.entity.Notification
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : JpaRepository<Notification, Long> {

    fun findByUser(user: User): Notification?
}
