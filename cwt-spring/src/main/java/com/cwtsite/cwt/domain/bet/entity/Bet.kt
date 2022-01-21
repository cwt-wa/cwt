package com.cwtsite.cwt.domain.bet.entity

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table(name = "bet")
@SequenceGenerator(name = "bet_seq", sequenceName = "bet_id_seq", allocationSize = 1)
data class Bet(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bet_seq")
    var id: Long? = null,

    @ManyToOne(optional = false, cascade = [CascadeType.MERGE, CascadeType.PERSIST])
    @JoinColumn(nullable = false)
    val user: User,

    @ManyToOne(optional = false, cascade = [CascadeType.MERGE, CascadeType.PERSIST])
    @JoinColumn(nullable = false)
    val game: Game,

    @Column(nullable = false)
    var betOnHome: Boolean,

    @field:UpdateTimestamp
    @Column(nullable = false)
    var modified: Instant? = null

) {

    override fun toString(): String {
        return "Bet{id=$id}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Bet
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
