package com.cwtsite.cwt.test

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
                   status: TournamentStatus = TournamentStatus.ARCHIVED) = Tournament(
            id = id,
            created = Timestamp.valueOf(created),
            maxRounds = maxRounds,
            moderators = moderators,
            status = status
    )
}
