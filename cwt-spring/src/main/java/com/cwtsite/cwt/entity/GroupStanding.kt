package com.cwtsite.cwt.entity

import com.cwtsite.cwt.domain.user.repository.entity.User
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
@Table(name = "group_standing")
@SequenceGenerator(name = "group_standing_seq", sequenceName = "group_standing_id_seq", allocationSize = 1)
data class GroupStanding(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_standing_seq")
    var id: Long? = null,

    @Column(name = "points")
    var points: Int = 0,

    @Column(name = "games")
    var games: Int = 0,

    @Column(name = "game_ratio")
    var gameRatio: Int = 0,

    @Column(name = "round_ratio")
    var roundRatio: Int = 0,

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    var user: User
) {

    constructor(user: User) : this(id = null, user = user)

    fun reset() {
        points = 0
        games = 0
        gameRatio = 0
        roundRatio = 0
    }

    override fun toString(): String {
        return "GroupStanding(id=$id, points=$points, games=$games, " +
            "gameRatio=$gameRatio, roundRatio=$roundRatio)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GroupStanding

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
