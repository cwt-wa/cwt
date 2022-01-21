package com.cwtsite.cwt.domain.stream.entity

import com.cwtsite.cwt.domain.game.entity.Game
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "stream")
data class Stream(

    @Id
    var id: String,

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    var channel: Channel,

    @Column(name = "user_id")
    var userId: String? = null,

    @Column(name = "user_name")
    var userName: String? = null,

    @Column(name = "title")
    var title: String? = null,

    @Column(name = "description")
    var description: String? = null,

    @Column(name = "created_at")
    var createdAt: String? = null,

    @Column(name = "published_at")
    var publishedAt: String? = null,

    @Column(name = "url")
    var url: String? = null,

    @Column(name = "thumbnail_url")
    var thumbnailUrl: String? = null,

    @Column(name = "viewable")
    var viewable: String? = null,

    @Column(name = "view_count")
    var viewCount: Long,

    @Column(name = "language")
    var language: String? = null,

    @Column(name = "type")
    var type: String? = null,

    @Column(name = "duration")
    var duration: String? = null,

    @ManyToOne
    var game: Game? = null
) {

    fun createdAtAsInstant(): Instant {
        val formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .withZone(ZoneId.of("UTC"))
        return LocalDateTime.parse(createdAt, formatter).toInstant(ZoneOffset.UTC)
    }

    fun relevantWordsInTitle(whitelist: Collection<String>): Set<String> {
        val blacklist = setOf(
            "cwt", "final", "finale", "quarter", "semi", "quarterfinal", "semifinal", "last",
            "group", "stage", "playoff", "playoffs", "vs", "round", "of", "-", "part", "round"
        )
            .filter { !whitelist.contains(it) }
        if (title == null) return emptySet()
        return title!!.toLowerCase()
            .split(Pattern.compile("\\W"))
            .asSequence()
            .filter { !blacklist.contains(it) }
            .filter { it.length >= 3 }
            .filter { !Regex("\\d{4}").matches(it) }
            .filter { it.contains(Regex("\\w")) }
            .map { it.trim() }
            .toSet()
    }

    /**
     * Update fields which are information from Twitch
     */
    // TODO exhaustion test
    fun updateTwitchInformation(dto: Stream) {
        userName = dto.userName
        title = dto.title
        description = dto.description
        createdAt = dto.createdAt
        publishedAt = dto.publishedAt
        url = dto.url
        thumbnailUrl = dto.thumbnailUrl
        viewable = dto.viewable
        viewCount = dto.viewCount
        language = dto.language
        type = dto.type
        duration = dto.duration
    }

    override fun toString(): String {
        return "Stream(id=$id)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Stream

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
