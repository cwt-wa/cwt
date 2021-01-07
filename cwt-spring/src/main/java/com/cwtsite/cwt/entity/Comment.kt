package com.cwtsite.cwt.entity

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import javax.persistence.*
import javax.validation.constraints.NotNull
import java.time.Instant
import java.util.Objects

@Entity
@Table(name = "comment")
@SequenceGenerator(name = "comment_seq", sequenceName = "comment_id_seq", allocationSize = 10)
data class Comment(

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_seq")
        var id: Long? = null,

        @NotNull
        @Column(name = "body", nullable = false, columnDefinition = "text")
        var body: String? = null,

        @Column(name = "created", nullable = false)
        @field:CreationTimestamp
        var created: Instant? = null,

        @Column(name = "modified")
        @field:UpdateTimestamp
        var modified: Instant? = null,

        @ManyToOne(optional = false)
        @JoinColumn(nullable = false)
        var author: User? = null,

        @JsonIgnore
        @ManyToOne(optional = false)
        @JoinColumn(nullable = false)
        var game: Game? = null
) {

    constructor(body: String, author: User, game: Game) : this(id = null, body = body, author = author, game = game)

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val comment = other as Comment?
        return if (comment!!.id == null || id == null) {
            false
        } else id == comment.id
    }

    override fun hashCode(): Int {
        return Objects.hashCode(id)
    }

    override fun toString(): String {
        return "Comment{" +
                "id=" + id +
                ", body='" + body + "'" +
                ", created='" + created + "'" +
                ", modified='" + modified + "'" +
                '}'.toString()
    }
}
