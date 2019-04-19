package com.cwtsite.cwt.test

import com.cwtsite.cwt.domain.game.entity.Game
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
                   status: TournamentStatus = TournamentStatus.FINISHED) = Tournament(
            id = id,
            created = Timestamp.valueOf(created),
            maxRounds = maxRounds,
            moderators = moderators,
            status = status
    )

    fun game(homeUser: User? = user(id = 1, username = "home"), awayUser: User? = user(id = 2, username = "away"),
             scoreHome: Int? = 3, scoreAway: Int? = 1, tournament: Tournament = tournament()) = Game(
            homeUser = homeUser,
            awayUser = awayUser,
            scoreHome = scoreHome,
            scoreAway = scoreAway,
            reporter = homeUser,
            tournament = tournament
    )
}
