package com.cwtsite.cwt.domain.game.entity

import com.cwtsite.cwt.domain.game.entity.enumeration.RatingType
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Timestamp
import javax.persistence.*

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
        var modified: Timestamp? = null
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