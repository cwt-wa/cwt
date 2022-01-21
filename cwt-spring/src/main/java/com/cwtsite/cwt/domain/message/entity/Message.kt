package com.cwtsite.cwt.domain.message.entity

import com.cwtsite.cwt.domain.message.entity.enumeration.MessageCategory
import com.cwtsite.cwt.domain.message.service.MessageNewsType
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table(name = "message")
@SequenceGenerator(name = "message_seq", sequenceName = "message_id_seq", allocationSize = 1)
data class Message(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_seq")
    var id: Long? = null,

    @Column(columnDefinition = "text", nullable = false)
    var body: String,

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    var author: User,

    @ManyToMany
    @JoinTable(
        name = "message_recipient",
        joinColumns = [JoinColumn(name = "MESSAGE_ID", referencedColumnName = "ID")],
        inverseJoinColumns = [JoinColumn(name = "USER_ID", referencedColumnName = "ID")]
    )
    var recipients: MutableList<User> = mutableListOf(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var category: MessageCategory = MessageCategory.SHOUTBOX,

    @Enumerated(EnumType.STRING)
    var newsType: MessageNewsType? = null,

    @field:CreationTimestamp
    @Column(nullable = false)
    var created: Instant? = null
) {
    init {
        if (recipients.isNotEmpty() && category != MessageCategory.PRIVATE) {
            throw IllegalStateException(
                "message is category $category (should be ${MessageCategory.PRIVATE})" +
                    " but has recipients $recipients"
            )
        }
        if (newsType != null && category != MessageCategory.NEWS) {
            throw IllegalStateException(
                "message has newsType $newsType but category is $category" +
                    " should be ${MessageCategory.NEWS}"
            )
        }
    }

    override fun toString() = "Message{id=$id, body=$body, category=$category, newsType=$newsType}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
