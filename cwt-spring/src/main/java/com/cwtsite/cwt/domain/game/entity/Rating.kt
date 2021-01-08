package com.cwtsite.cwt.domain.game.entity

import com.cwtsite.cwt.domain.game.entity.enumeration.RatingType
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Enumerated
import javax.persistence.EnumType
import javax.persistence.GenerationType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table(name = "rating")
@SequenceGenerator(name = "rating_seq", sequenceName = "rating_id_seq", allocationSize = 10)
data class Rating(

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rating_seq")
        var id: Long? = null,

        @Column(name = "type")
        @Enumerated(EnumType.STRING)
        var type: RatingType,

        @ManyToOne(optional = false)
        @JoinColumn(nullable = false)
        var user: User,

        @JsonIgnore
        @ManyToOne(optional = false)
        @JoinColumn(nullable = false)
        var game: Game,

        @Column(name = "modified", nullable = false)
        @field:UpdateTimestamp
        var modified: Instant? = null
) {

    override fun toString(): String =
            "Rating{id=$id, type=$type}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Rating
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
