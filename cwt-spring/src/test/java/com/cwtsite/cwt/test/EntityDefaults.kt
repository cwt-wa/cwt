package com.cwtsite.cwt.test

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.PlayoffGame
import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.user.repository.entity.User
import java.sql.Timestamp
import java.time.LocalDateTime


object EntityDefaults {

    fun user(id: Long = 1, username: String = "Zemke", email: String = "zemke@zemke") = User(
            id = id,
            username = username,
            email = email
    )

    fun tournament(created: LocalDateTime = LocalDateTime.now(), id: Long = 1, maxRounds: Int = 5, moderators: Set<User> = setOf(user()),
                   threeWay: Boolean  = false, status: TournamentStatus = TournamentStatus.FINISHED) = Tournament(
            id = id,
            created = Timestamp.valueOf(created),
            maxRounds = maxRounds,
            moderators = moderators,
            threeWay = threeWay,
            status = status
    )

    fun game(id: Long = 1, homeUser: User? = user(id = 1, username = "home"), awayUser: User? = user(id = 2, username = "away"),
             scoreHome: Int? = 3, scoreAway: Int? = 1, tournament: Tournament = tournament(), playoff: PlayoffGame? = null) = Game(
            id = id,
            homeUser = homeUser,
            awayUser = awayUser,
            scoreHome = scoreHome,
            scoreAway = scoreAway,
            reporter = homeUser,
            playoff = playoff,
            tournament = tournament
    )

    fun channel(id: String = "1111", title: String = "TitleTV", user: User = user()) = Channel(
            id = id,
            title = title,
            user = user
    )
}
