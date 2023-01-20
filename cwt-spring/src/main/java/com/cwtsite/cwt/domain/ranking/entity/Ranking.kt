package com.cwtsite.cwt.domain.ranking.entity

import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.Instant
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.validation.constraints.Digits

@Entity
@Table(name = "ranking")
data class Ranking(

    @Id
    @Column(name = "user_id")
    var userId: Long? = null,

    @OneToOne(cascade = [CascadeType.MERGE])
    @JoinColumn(name = "user_id")
    @MapsId
    var user: User,

    /**
     * Last tournament participated in.
     */
    @ManyToOne
    var lastTournament: Tournament? = null,

    /**
     * Zero-based index sorted by points descendingly in ranking after last finished or archived tournament.
     */
    var lastPlace: Int = 0,

    /**
     * Change in places in this ranking since last finished or archived tournament.
     */
    var lastDiff: Int = 0,

    @Digits(integer = 5, fraction = 5)
    var points: BigDecimal = BigDecimal.ZERO,

    var participations: Int = 0,

    var gold: Int = 0,

    var silver: Int = 0,

    var bronze: Int = 0,

    var played: Int = 0,

    var won: Int = 0,

    var lost: Int = 0,

    var wonRatio: Double = .0,

    @field:UpdateTimestamp
    @Column(name = "modified", nullable = false)
    var modified: Instant? = null,
) {
    init {
        userId = user.id
    }

    override fun toString(): String {
        return "Ranking(user=$user, lastTournament=$lastTournament, lastDiff=$lastDiff, points=$points, participations=$participations, gold=$gold, silver=$silver, bronze=$bronze, played=$played, won=$won, lost=$lost, wonRatio=$wonRatio)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Ranking
        if (user.id != other.user.id) return false
        return true
    }

    override fun hashCode(): Int {
        return user.id?.hashCode() ?: 0
    }
}
