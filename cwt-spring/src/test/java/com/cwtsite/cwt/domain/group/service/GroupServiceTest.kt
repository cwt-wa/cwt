package com.cwtsite.cwt.domain.group.service

import com.cwtsite.cwt.domain.configuration.service.ConfigurationService
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.group.entity.Group
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.entity.GroupStanding
import com.cwtsite.cwt.test.EntityDefaults
import org.assertj.core.api.Assertions
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class GroupServiceTest {

    @InjectMocks private lateinit var groupService: GroupService
    @Mock private lateinit var groupRepository: GroupRepository
    @Mock private lateinit var groupStandingRepository: GroupStandingRepository
    @Mock private lateinit var tournamentRepository: TournamentRepository
    @Mock private lateinit var configurationService: ConfigurationService
    @Mock private lateinit var userRepository: UserRepository
    @Mock private lateinit var tournamentService: TournamentService
    @Mock private lateinit var gameRepository: GameRepository

    @Test
    fun calcTableByGame() {
        val game = Game(tournament = EntityDefaults.tournament(status = TournamentStatus.GROUP))

        val user1 = EntityDefaults.user(1)
        val user2 = EntityDefaults.user(2)

        game.homeUser = user1
        game.awayUser = user2
        game.scoreHome = 3
        game.scoreAway = 2
        game.group = createGroup(user1, user2)

        Mockito
                .`when`(configurationService.parsedPointsPatternConfiguration)
                .thenReturn(listOf(intArrayOf(3, 3), intArrayOf(2, 1)))

        Mockito
                .`when`(groupStandingRepository.saveAll(Mockito.anyIterable<GroupStanding>() as MutableIterable<GroupStanding>))
                .thenAnswer { invocation ->
                    val actualStandings = invocation.getArgument<List<GroupStanding>>(0)
                    val winnerStandings = actualStandings[0]
                    val loserStandings = actualStandings[1]

                    Assert.assertEquals(user1, winnerStandings.user)
                    Assert.assertEquals(2, winnerStandings.games)
                    Assert.assertEquals(6, winnerStandings.points)
                    Assert.assertEquals(2, winnerStandings.gameRatio)
                    Assert.assertEquals(4, winnerStandings.roundRatio)

                    Assert.assertEquals(user2, loserStandings.user)
                    Assert.assertEquals(2, loserStandings.games)
                    Assert.assertEquals(4, loserStandings.points)
                    Assert.assertEquals(0, loserStandings.gameRatio)
                    Assert.assertEquals(2, loserStandings.roundRatio)

                    actualStandings.asIterable()
                }

        groupService.calcTableByGame(game)
    }

    private fun createGroup(user1: User, user2: User): Group {
        val group = Group()
        group.standings.addAll(listOf(createGroupStanding(user1, group), createGroupStanding(user2, group)))
        return group
    }

    private fun createGroupStanding(user: User, group: Group): GroupStanding {
        val groupStanding = GroupStanding(user)
        groupStanding.user = user
        groupStanding.games = 1
        groupStanding.points = 3
        groupStanding.gameRatio = 1
        groupStanding.roundRatio = 3
        return groupStanding
    }

    @Test
    fun replacePlayer() {
        val idOfUserObsolete = 1L
        val idOfUserNew = 2L
        val obsoleteUser = EntityDefaults.user(id = idOfUserObsolete)
        val newUser = EntityDefaults.user(id = idOfUserNew)
        val tournament = EntityDefaults.tournament(status = TournamentStatus.GROUP)
        val user22 = EntityDefaults.user(id = 22)
        val user33 = EntityDefaults.user(id = 33)
        val user44 = EntityDefaults.user(id = 44)
        val group = with(Group(id = 1, tournament = tournament)) {
            standings.addAll(listOf(
                    GroupStanding(
                            id = 1,
                            user = obsoleteUser,
                            games = 2,
                            gameRatio = 0,
                            roundRatio = 2,
                            points = 4
                    ),
                    GroupStanding(
                            id = 2,
                            user = user22,
                            games = 3,
                            gameRatio = 3,
                            roundRatio = -1,
                            points = 4
                    ),
                    GroupStanding(
                            id = 3,
                            user = user33,
                            games = 2,
                            gameRatio = 2,
                            roundRatio = 2,
                            points = 6
                    ),
                    GroupStanding(
                            id = 4,
                            user = user44,
                            games = 1,
                            gameRatio = -1,
                            roundRatio = -3,
                            points = 0
                    )
            ))
            this
        }
        val games = listOf(
                Game(
                        id = 1,
                        homeUser = user33,
                        awayUser = obsoleteUser,
                        scoreHome = 3,
                        scoreAway = 2,
                        group = group,
                        tournament = tournament
                ),
                Game(
                        id = 2,
                        homeUser = obsoleteUser,
                        awayUser = user22,
                        scoreHome = 3,
                        scoreAway = 0,
                        group = group,
                        tournament = tournament
                ),
                Game(
                        id = 3,
                        homeUser = user33,
                        awayUser = user22,
                        scoreHome = 3,
                        scoreAway = 2,
                        group = group,
                        tournament = tournament
                ),
                Game(
                        id = 4,
                        homeUser = user44,
                        awayUser = user22,
                        scoreHome = 0,
                        scoreAway = 3,
                        group = group,
                        tournament = tournament
                )
        ).map { Mockito.spy(it) }

        Mockito
                .`when`(userRepository.findById(idOfUserObsolete))
                .thenReturn(Optional.of(obsoleteUser))

        Mockito
                .`when`(userRepository.findById(idOfUserNew))
                .thenReturn(Optional.of(newUser))

        Mockito
                .`when`(tournamentService.getCurrentTournament())
                .thenReturn(tournament)

        Mockito
                .`when`(groupRepository.findByTournamentAndUser(tournament, obsoleteUser))
                .thenReturn(group)

        Mockito
                .`when`(gameRepository.findByGroup(group))
                .thenReturn(games)

        Mockito
                .`when`(configurationService.parsedPointsPatternConfiguration)
                .thenReturn(listOf(intArrayOf(3, 3), intArrayOf(2, 1)))

        val groupAfterReplacement = groupService.replacePlayer(idOfUserObsolete, idOfUserNew)

        Assertions.assertThat(groupAfterReplacement.standings.find { it.user == obsoleteUser }).isNull()
        Assertions.assertThat(groupAfterReplacement.standings.find { it.user == newUser }).isNotNull

        games
                .filter { it.pairingInvolves(obsoleteUser) }
                .forEach { Mockito.verify(it).voided = true }

        Assertions.assertThat(groupAfterReplacement.standings.find { it.user == newUser }).isEqualTo(GroupStanding(
                id = 1,
                user = newUser,
                games = 0,
                gameRatio = 0,
                roundRatio = 0,
                points = 0
        ))
        Assertions.assertThat(groupAfterReplacement.standings.find { it.user == user22 }).isEqualTo(GroupStanding(
                id = 2,
                user = user22,
                games = 2,
                gameRatio = 0,
                roundRatio = 2,
                points = 4
        ))
        Assertions.assertThat(groupAfterReplacement.standings.find { it.user == user33 }).isEqualTo(GroupStanding(
                id = 3,
                user = user33,
                games = 1,
                gameRatio = 1,
                roundRatio = 1,
                points = 3
        ))
        Assertions.assertThat(groupAfterReplacement.standings.find { it.user == user44 }).isEqualTo(GroupStanding(
                id = 4,
                user = user44,
                games = 1,
                gameRatio = -1,
                roundRatio = -3,
                points = 0
        ))
    }
}
