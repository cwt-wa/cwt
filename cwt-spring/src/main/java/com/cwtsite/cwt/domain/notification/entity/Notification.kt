package com.cwtsite.cwt.domain.notification.entity

import com.cwtsite.cwt.domain.user.repository.entity.User
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table
@SequenceGenerator(name = "notification_seq", sequenceName = "notification_seq", allocationSize = 1)
data class Notification(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq")
    var id: Long? = null,

    @ManyToOne(optional = false, cascade = [CascadeType.MERGE, CascadeType.PERSIST])
    @JoinColumn(nullable = false)
    var user: User,

    @Column(name = "subscription", columnDefinition = "text", nullable = false)
    var subscription: String,

    @Column(name = "subscription_created", nullable = false)
    var subscriptionCreated: Instant? = null,

    @Column(name = "setting", nullable = false)
    var setting: Int,

    @Column(name = "user_agent", columnDefinition = "text")
    var userAgent: String?,

    @field:UpdateTimestamp
    @Column(name = "modified", nullable = false, updatable = false)
    var modified: Instant? = null,

    @field:CreationTimestamp
    @Column(name = "created", nullable = false, updatable = false)
    var created: Instant? = null,
)
