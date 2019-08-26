package com.cwtsite.cwt.domain.game.entity

import javax.persistence.*

@Entity
@Table(name = "playoff_game")
@SequenceGenerator(name = "playoff_game_seq", sequenceName = "playoff_game_id_seq")
data class PlayoffGame(

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "playoff_game_seq")
        var id: Long? = null,

        @Column(name = "round")
        var round: Int,

        @Column(name = "spot")
        var spot: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlayoffGame

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
