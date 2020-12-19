package com.cwtsite.cwt.domain.stream.entity

import com.cwtsite.cwt.domain.game.entity.Game
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.persistence.*

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

    fun createdAtAsTimestamp(): Timestamp {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        format.timeZone = TimeZone.getTimeZone("UTC")
        return Timestamp(format.parse(createdAt).time)
    }

    fun relevantWordsInTitle(whitelist: Collection<String>): Set<String> {
        val blacklist = setOf(
                "cwt", "final", "finale", "quarter", "semi", "quarterfinal", "semifinal", "last",
                "group", "stage", "playoff", "playoffs", "vs", "round", "of", "-", "part", "round")
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
