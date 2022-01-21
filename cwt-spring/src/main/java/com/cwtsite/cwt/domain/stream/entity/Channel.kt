package com.cwtsite.cwt.domain.stream.entity

import com.cwtsite.cwt.domain.user.repository.entity.User
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "channel")
data class Channel(

    /**
     * The channel ID derived from the user ID from the Twitch API.
     */
    @Id
    var id: String,

    @OneToOne(optional = false)
    @JoinColumn(nullable = false)
    val user: User,

    /**
     * The latest pagination cursor for the videos endpoint from the Twitch API.
     */
    @Column(name = "video_cursor")
    var videoCursor: String? = null,

    /**
     * On CWT users can give their channel a unique name independent from what it's called on Twitch.
     */
    @Column(name = "title", nullable = false, unique = true)
    val title: String,

    @Column(name = "display_name")
    var displayName: String? = null,

    @Column(name = "type")
    var type: String? = null,

    @Column(name = "profile_image_url")
    var profileImageUrl: String? = null,

    @Column(name = "view_count")
    var viewCount: Long? = null,

    @Column(name = "broadcaster_type")
    var broadcasterType: String? = null,

    @Column(name = "offline_image_url")
    var offlineImageUrl: String? = null,

    @Column(name = "login")
    var login: String? = null,

    @Column(name = "description")
    var description: String? = null,

    @Column
    var botAutoJoin: Boolean = false,

    @field:UpdateTimestamp
    @Column(nullable = false)
    var modified: Instant? = null,

    @field:CreationTimestamp
    @Column(nullable = false, insertable = false, updatable = false)
    var created: Instant? = null
) {

    override fun toString(): String {
        return "Channel{id=$id}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Channel

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
