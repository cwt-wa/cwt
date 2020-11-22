package com.cwtsite.cwt.test

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.PlayoffGame
import com.cwtsite.cwt.domain.group.entity.Group
import com.cwtsite.cwt.domain.schedule.entity.Schedule
import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.stream.entity.Stream
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

    fun tournament(created: LocalDateTime = LocalDateTime.now(), id: Long = 1, maxRounds: Int = 5, numOfGroupAdvancing: Int = 2,
                   moderators: MutableSet<User> = mutableSetOf(user()), threeWay: Boolean  = false,
                   status: TournamentStatus = TournamentStatus.FINISHED) = Tournament(
            id = id,
            created = Timestamp.valueOf(created),
            maxRounds = maxRounds,
            numOfGroupAdvancing = numOfGroupAdvancing,
            moderators = moderators,
            threeWay = threeWay,
            status = status
    )

    fun game(id: Long = 1, homeUser: User? = user(id = 1, username = "home"), awayUser: User? = user(id = 2, username = "away"),
             scoreHome: Int? = 3, scoreAway: Int? = 1, tournament: Tournament = tournament(),
             playoff: PlayoffGame? = null, group: Group? = null, reportedAt: Timestamp? = null) = Game(
            id = id,
            homeUser = homeUser,
            awayUser = awayUser,
            scoreHome = scoreHome,
            scoreAway = scoreAway,
            reporter = homeUser,
            playoff = playoff,
            group = group,
            tournament = tournament,
            modified = Timestamp(1577833200000),
            created = Timestamp(1577833200000), // 1st Jan 2020
            reportedAt = reportedAt
    )

    fun channel(id: String = "1111", title: String = "TitleTV", user: User = user()) = Channel(
            id = id,
            title = title,
            user = user
    )

    fun stream(title: String = "epic stream", id: String = "1", viewCount: Long = 2,
               game: Game? = null, channel: Channel = channel()) =
            Stream(title = title, id = id, viewCount = viewCount, channel = channel,
                    game = game, createdAt = "2014-10-18T22:28:17Z")

    fun schedule(id: Long = 1, appointment: Timestamp = Timestamp(1606079536669),
                 created: Timestamp = Timestamp(1606079436669), author: User = user(id = 1),
                 homeUser: User = user(id = 1), awayUser: User = user(id = 2),
                 streams: MutableSet<Channel> = mutableSetOf()) =
            Schedule(
                    id = id,
                    appointment = appointment,
                    homeUser = homeUser,
                    awayUser = awayUser,
                    author = author,
                    created = created,
                    streams = streams)
}
