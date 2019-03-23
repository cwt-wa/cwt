package com.cwtsite.cwt.entity

import com.cwtsite.cwt.domain.user.repository.entity.User
import javax.persistence.*

@Entity
@Table(name = "group_standing")
@SequenceGenerator(name = "group_standing_seq", sequenceName = "group_standing_seq")
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

//        @JsonIgnore
//        @ManyToOne
//        @JoinColumn(name = "group_id")
//        var group: Group,

        @ManyToOne
        var user: User
) {

    constructor(user: User) : this(id = null, user = user)

    fun reset() {
        points = 0
        games = 0
        gameRatio = 0
        roundRatio = 0
    }
}
