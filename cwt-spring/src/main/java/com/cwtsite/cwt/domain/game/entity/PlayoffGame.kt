package com.cwtsite.cwt.domain.game.entity

import javax.persistence.*

@Entity
@Table(name = "playoff_game")
@SequenceGenerator(name = "playoff_game_seq", sequenceName = "playoff_game_seq", allocationSize = 1)
data class PlayoffGame(

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "playoff_game_seq")
        var id: Long? = null,

        @Column(name = "round")
        var round: Int,

        @Column(name = "spot")
        var spot: Int
)
