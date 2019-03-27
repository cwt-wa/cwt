package com.cwtsite.cwt.domain.message.entity

import com.cwtsite.cwt.domain.message.entity.enumeration.MessageCategory
import com.cwtsite.cwt.domain.message.service.MessageNewsType
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.hibernate.annotations.CreationTimestamp
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "message")
@SequenceGenerator(name = "message_seq", sequenceName = "message_seq", allocationSize = 1)
data class Message(

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_seq")
        var id: Long? = null,

        @Column(columnDefinition = "text", nullable = false)
        var body: String,

        @ManyToOne(optional =  false)
        @JoinColumn(nullable = false)
        var author: User,

        @ManyToMany
        @JoinTable(
                name = "message_recipient",
                joinColumns = [JoinColumn(name = "USER_ID", referencedColumnName = "ID")],
                inverseJoinColumns = [JoinColumn(name = "MESSAGE_ID", referencedColumnName = "ID")])
        val recipients: List<User> = mutableListOf(),

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        var category: MessageCategory = MessageCategory.SHOUTBOX,

        @Enumerated(EnumType.STRING)
        var newsType: MessageNewsType? = null,

        @field:CreationTimestamp
        @Column(nullable = false)
        var created: Timestamp? = null
)
