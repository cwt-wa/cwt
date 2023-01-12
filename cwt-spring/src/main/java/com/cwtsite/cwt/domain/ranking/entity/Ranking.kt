package com.cwtsite.cwt.domain.ranking.entity

import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.user.repository.entity.User
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.validation.constraints.Digits

@Entity
@Table(name = "ranking")
@SequenceGenerator(name = "ranking_seq", sequenceName = "ranking_id_seq", allocationSize = 1)
data class Ranking(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ranking_seq")
    var id: Long? = null,

    @OneToOne
    var user: User? = null,

    /**
     * Last tournament participated in.
     */
    @ManyToOne
    var last: Tournament? = null,

    /**
     * Change in places in this ranking since last tournament.
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
) {

    override fun toString(): String {
        return "Ranking(id=$id, user=$user, last=$last, lastDiff=$lastDiff, points=$points, participations=$participations, gold=$gold, silver=$silver, bronze=$bronze, played=$played, won=$won, lost=$lost, wonRatio=$wonRatio)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Ranking
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
