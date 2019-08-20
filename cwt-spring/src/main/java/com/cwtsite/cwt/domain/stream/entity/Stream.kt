package com.cwtsite.cwt.domain.stream.entity

import javax.persistence.*

@Entity
@Table(name = "stream")
data class Stream(

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stream_id_seq")
        @SequenceGenerator(name = "stream_id_seq", sequenceName = "stream_id_seq")
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
        var duration: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Stream

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode() ?: 0
    }

    fun hasCwtInTitle(): Boolean = title?.contains(Regex("""\bcwt\b""", RegexOption.IGNORE_CASE)) ?: false
}
