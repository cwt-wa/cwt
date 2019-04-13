package com.cwtsite.cwt.domain.playoffs.service

import com.cwtsite.cwt.domain.configuration.entity.Configuration
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.configuration.service.ConfigurationService
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.PlayoffGame
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.group.service.GroupRepository
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.test.EntityDefaults
import com.cwtsite.cwt.test.MockitoUtils
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

/**
 * Playoff tree with six players, three games in first round
 * and then three-way final.
 *
 * ```
 *        1      2    Round
 *
 *    1  OO \
 *  1        \  OO
 *    2  OO --- OO
 *  2        /  OO
 *    3  OO /
 *
 *  Spot
 * ```
 *
 * Three-way final in SpotvsSpot format: 1vs2 2vs3 3vs1
 */
@RunWith(MockitoJUnitRunner::class)
class PlayoffServiceThreeWayFinalTest {

    @InjectMocks
    private lateinit var playoffService: PlayoffService

    @Mock
    private lateinit var gameRepository: GameRepository

    @Mock
    private lateinit var configurationService: ConfigurationService

    @Mock
    private lateinit var groupRepository: GroupRepository

    @Mock
    private lateinit var tournamentService: TournamentService

    private val tournament = EntityDefaults.tournament(status = TournamentStatus.PLAYOFFS, maxRounds = 2)

    @Before
    fun initMocks() {
        Mockito
                .`when`(groupRepository.countByTournament(MockitoUtils.anyObject<Tournament>()))
                .thenReturn(3)

        Mockito
                .`when`(configurationService.getOne(ConfigurationKey.NUMBER_OF_GROUP_MEMBERS_ADVANCING))
                .thenReturn(Configuration(
                        key = ConfigurationKey.NUMBER_OF_GROUP_MEMBERS_ADVANCING,
                        value = "2",
                        author = EntityDefaults.user()
                ))

        Mockito
                .`when`(gameRepository.save(MockitoUtils.anyObject<Game>()))
                .thenAnswer { it.getArgument<Game>(0) }

        Mockito
                .`when`(gameRepository.findByTournamentAndRound(tournament, 1))
                .thenReturn(Mockito.spy<ArrayList<Game>>(object : ArrayList<Game>() {
                    override val size: Int
                        get() = 3
                }))
    }

    /**
     * Three-way final games do not yet exist, it's the first semi finalist to reach the final.
     */
    @Test
    fun advanceByGame_firstToReachFinal() {
        val game = Game(
                scoreHome = 3,
                scoreAway = 1,
                homeUser = EntityDefaults.user(),
                awayUser = EntityDefaults.user(id = 2),
                playoff = PlayoffGame(round = 1, spot = 2),
                tournament = tournament
        )

        val gameToAdvanceTo = playoffService.advanceByGame(game)

        Assertions.assertThat(gameToAdvanceTo[0].awayUser).isNull()
        Assertions.assertThat(gameToAdvanceTo[0].homeUser).isEqualTo(game.homeUser)
        Assertions.assertThat(gameToAdvanceTo[0].playoff!!.round).isEqualTo(2)
        Assertions.assertThat(gameToAdvanceTo[0].playoff!!.spot).isEqualTo(1)
        Assertions.assertThat(gameToAdvanceTo[0].tournament).isEqualTo(tournament)

        Assertions.assertThat(gameToAdvanceTo[1].awayUser).isEqualTo(game.homeUser)
        Assertions.assertThat(gameToAdvanceTo[1].homeUser).isNull()
        Assertions.assertThat(gameToAdvanceTo[1].playoff!!.round).isEqualTo(2)
        Assertions.assertThat(gameToAdvanceTo[1].playoff!!.spot).isEqualTo(2)
        Assertions.assertThat(gameToAdvanceTo[1].tournament).isEqualTo(tournament)

        Assertions.assertThat(gameToAdvanceTo[2].awayUser).isNull()
        Assertions.assertThat(gameToAdvanceTo[2].homeUser).isNull()
        Assertions.assertThat(gameToAdvanceTo[2].playoff!!.round).isEqualTo(2)
        Assertions.assertThat(gameToAdvanceTo[2].playoff!!.spot).isEqualTo(3)
        Assertions.assertThat(gameToAdvanceTo[2].tournament).isEqualTo(tournament)

        Assertions.assertThat(gameToAdvanceTo.size).isEqualTo(3)
    }

    /**
     * Three-way final games have been created now a second three-way finalist joins.
     */
    @Test
    fun advanceByGame_secondToReachFinal() {
        val firstUserToReachFinals = EntityDefaults.user(1)
        val game = Game(
                scoreHome = 3,
                scoreAway = 1,
                homeUser = EntityDefaults.user(),
                awayUser = EntityDefaults.user(id = 22),
                playoff = PlayoffGame(round = 1, spot = 2),
                tournament = tournament
        )

        Mockito
                .`when`(gameRepository.findByTournamentAndRound(tournament, 2))
                .thenReturn(listOf(
                        Game(
                                awayUser = null,
                                homeUser = firstUserToReachFinals,
                                playoff = PlayoffGame(round = 2, spot = 1),
                                tournament = tournament
                        ),
                        Game(
                                awayUser = firstUserToReachFinals,
                                homeUser = null,
                                playoff = PlayoffGame(round = 2, spot = 2),
                                tournament = tournament
                        ),
                        Game(
                                awayUser = null,
                                homeUser = null,
                                playoff = PlayoffGame(round = 2, spot = 3),
                                tournament = tournament
                        )
                ))

        val gameToAdvanceTo = playoffService.advanceByGame(game)

        Assertions.assertThat(gameToAdvanceTo[0].awayUser).isEqualTo(game.homeUser)
        Assertions.assertThat(gameToAdvanceTo[0].homeUser).isEqualTo(firstUserToReachFinals)
        Assertions.assertThat(gameToAdvanceTo[0].playoff!!.round).isEqualTo(2)
        Assertions.assertThat(gameToAdvanceTo[0].playoff!!.spot).isEqualTo(1)
        Assertions.assertThat(gameToAdvanceTo[0].tournament).isEqualTo(tournament)

        Assertions.assertThat(gameToAdvanceTo[1].awayUser).isNull()
        Assertions.assertThat(gameToAdvanceTo[1].homeUser).isEqualTo(game.homeUser)
        Assertions.assertThat(gameToAdvanceTo[1].playoff!!.round).isEqualTo(2)
        Assertions.assertThat(gameToAdvanceTo[1].playoff!!.spot).isEqualTo(3)
        Assertions.assertThat(gameToAdvanceTo[1].tournament).isEqualTo(tournament)

        Assertions.assertThat(gameToAdvanceTo.size).isEqualTo(2)
    }

    /**
     * Last three-way finalist to join.
     */
    @Test
    fun advanceByGame_thirdToReachFinal() {
        val firstUserToReachFinals = EntityDefaults.user(1)
        val secondUserToReachFinals = EntityDefaults.user(2)
        val game = Game(
                scoreHome = 3,
                scoreAway = 1,
                homeUser = EntityDefaults.user(),
                awayUser = EntityDefaults.user(id = 28),
                playoff = PlayoffGame(round = 1, spot = 3),
                tournament = tournament
        )

        Mockito
                .`when`(gameRepository.findByTournamentAndRound(tournament, 2))
                .thenReturn(listOf(
                        Game(
                                awayUser = secondUserToReachFinals,
                                homeUser = firstUserToReachFinals,
                                playoff = PlayoffGame(round = 2, spot = 1),
                                tournament = tournament
                        ),
                        Game(
                                awayUser = firstUserToReachFinals,
                                homeUser = null,
                                playoff = PlayoffGame(round = 2, spot = 2),
                                tournament = tournament
                        ),
                        Game(
                                awayUser = null,
                                homeUser = secondUserToReachFinals,
                                playoff = PlayoffGame(round = 2, spot = 3),
                                tournament = tournament
                        )
                ))

        val gameToAdvanceTo = playoffService.advanceByGame(game)

        Assertions.assertThat(gameToAdvanceTo[0].awayUser).isEqualTo(firstUserToReachFinals)
        Assertions.assertThat(gameToAdvanceTo[0].homeUser).isEqualTo(game.homeUser)
        Assertions.assertThat(gameToAdvanceTo[0].playoff!!.round).isEqualTo(2)
        Assertions.assertThat(gameToAdvanceTo[0].playoff!!.spot).isEqualTo(2)
        Assertions.assertThat(gameToAdvanceTo[0].tournament).isEqualTo(tournament)

        Assertions.assertThat(gameToAdvanceTo[1].awayUser).isEqualTo(game.homeUser)
        Assertions.assertThat(gameToAdvanceTo[1].homeUser).isEqualTo(secondUserToReachFinals)
        Assertions.assertThat(gameToAdvanceTo[1].playoff!!.round).isEqualTo(2)
        Assertions.assertThat(gameToAdvanceTo[1].playoff!!.spot).isEqualTo(3)
        Assertions.assertThat(gameToAdvanceTo[1].tournament).isEqualTo(tournament)

        Assertions.assertThat(gameToAdvanceTo.size).isEqualTo(2)
    }

    /**
     * Three-way final game—nothing to advance to.
     */
    @Test
    fun advanceByGame_threeWayFinalPlayed() {
        val game = Game(
                scoreHome = 3,
                scoreAway = 1,
                homeUser = EntityDefaults.user(),
                awayUser = EntityDefaults.user(id = 2),
                playoff = PlayoffGame(round = 2, spot = 2),
                tournament = tournament
        )

        Assertions.assertThat(playoffService.advanceByGame(game)).isEmpty()
    }

    // TODO Archive tournament and winner, runner-up and third can be determined.
}
