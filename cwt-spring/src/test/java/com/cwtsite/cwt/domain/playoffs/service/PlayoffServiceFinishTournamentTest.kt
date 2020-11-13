package com.cwtsite.cwt.domain.playoffs.service

import com.cwtsite.cwt.domain.configuration.entity.Configuration
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.configuration.service.ConfigurationRepository
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.PlayoffGame
import com.cwtsite.cwt.domain.game.service.GameRepository
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
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class PlayoffServiceFinishTournamentTest {

    @InjectMocks private lateinit var playoffService: PlayoffService
    @Mock private lateinit var gameRepository: GameRepository
    @Mock private lateinit var configurationRepository: ConfigurationRepository
    @Mock private lateinit var tournamentService: TournamentService
    @Mock private lateinit var treeService: TreeService

    private val tournament = EntityDefaults.tournament(status = TournamentStatus.PLAYOFFS)

    @Test
    fun advanceByGame_isFinalGame() {
        val (finalGame, thirdPlaceGame) = createOneWayFinalGames()

        lenient().`when`(treeService.isFinalGame(MockitoUtils.anyObject(), anyInt())).thenReturn(true)
        lenient().`when`(treeService.isThirdPlaceGame(MockitoUtils.anyObject(), anyInt())).thenReturn(false)
        lenient().`when`(treeService.isThreeWayFinalGame(MockitoUtils.anyObject(), anyInt())).thenReturn(false)

        `when`(gameRepository.findByTournamentAndRoundAndNotVoided(tournament, finalGame.playoff!!.round - 1))
                .thenReturn(listOf(thirdPlaceGame))

        playoffService.advanceByGame(finalGame)

        verify(tournamentService).finish(finalGame.winner(), finalGame.loser(), thirdPlaceGame.winner())
    }

    @Test
    fun advanceByGame_isThirdPlaceGame() {
        val (finalGame, thirdPlaceGame) = createOneWayFinalGames()

        lenient().`when`(treeService.isFinalGame(MockitoUtils.anyObject(), anyInt())).thenReturn(false)
        lenient().`when`(treeService.isThirdPlaceGame(MockitoUtils.anyObject(), anyInt())).thenReturn(true)
        lenient().`when`(treeService.isThreeWayFinalGame(MockitoUtils.anyObject(), anyInt())).thenReturn(false)

        `when`(gameRepository.findByTournamentAndRoundAndNotVoided(tournament, thirdPlaceGame.playoff!!.round + 1))
                .thenReturn(listOf(finalGame))

        playoffService.advanceByGame(thirdPlaceGame)

        verify(tournamentService).finish(finalGame.winner(), finalGame.loser(), thirdPlaceGame.winner())
    }

    @Test
    fun advanceByGame_isThreeWayFinalGame() {
        val user1 = EntityDefaults.user(id = 1, username = "user1")
        val user2 = EntityDefaults.user(id = 2, username = "user2")
        val user3 = EntityDefaults.user(id = 3, username = "user3")

        val game = Game(
                id = 3,
                scoreHome = 4,
                scoreAway = 1,
                homeUser = user1,
                awayUser = user3,
                playoff = PlayoffGame(round = 2, spot = 2),
                tournament = tournament
        )

        lenient().`when`(treeService.isFinalGame(MockitoUtils.anyObject(), anyInt())).thenReturn(false)
        lenient().`when`(treeService.isThirdPlaceGame(MockitoUtils.anyObject(), anyInt())).thenReturn(false)
        lenient().`when`(treeService.isThreeWayFinalGame(MockitoUtils.anyObject(), anyInt())).thenReturn(true)

        `when`(gameRepository.findByTournamentAndRoundAndNotVoided(tournament, game.playoff!!.round))
                .thenReturn(listOf(
                        EntityDefaults.game(
                                id = 1,
                                homeUser = user1,
                                awayUser = user2,
                                scoreHome = 4,
                                scoreAway = 1,
                                playoff = PlayoffGame(round = game.playoff!!.round, spot = 1),
                                tournament = tournament),
                        EntityDefaults.game(
                                id = 2,
                                homeUser = user3,
                                awayUser = user2,
                                scoreHome = 4,
                                scoreAway = 1,
                                playoff = PlayoffGame(round = game.playoff!!.round, spot = 3),
                                tournament = tournament),
                        game.copy(scoreHome = null, scoreAway = null, reporter = null)
                ))

        playoffService.advanceByGame(game)

        verify(tournamentService).finish(user1, user3, user2)
    }

    @Test
    fun advanceByGame_isTiedThreeWayFinalGame() {
        val user1 = EntityDefaults.user(id = 1, username = "user1")
        val user2 = EntityDefaults.user(id = 2, username = "user2")
        val user3 = EntityDefaults.user(id = 3, username = "user3")

        val game = Game(
                id = 2,
                scoreHome = 3,
                scoreAway = 1,
                homeUser = user3,
                awayUser = user1,
                playoff = PlayoffGame(round = 2, spot = 2),
                tournament = tournament
        )

        val threeWayFinalGames = listOf(
                EntityDefaults.game(
                        id = 1,
                        homeUser = user1,
                        awayUser = user2,
                        scoreHome = 4,
                        scoreAway = 1,
                        playoff = PlayoffGame(round = 2, spot = 1),
                        tournament = tournament),
                EntityDefaults.game(
                        id = 2,
                        homeUser = user3,
                        awayUser = user1,
                        scoreHome = 4,
                        scoreAway = 1,
                        playoff = PlayoffGame(round = 2, spot = 2),
                        tournament = tournament),
                EntityDefaults.game(
                        id = 3,
                        homeUser = user2,
                        awayUser = user3,
                        scoreHome = 4,
                        scoreAway = 1,
                        playoff = PlayoffGame(round = 2, spot = 3),
                        tournament = tournament)
        )

        lenient().`when`(treeService.isFinalGame(MockitoUtils.anyObject(), anyInt())).thenReturn(false)
        lenient().`when`(treeService.isThirdPlaceGame(MockitoUtils.anyObject(), anyInt())).thenReturn(false)
        lenient().`when`(treeService.isThreeWayFinalGame(MockitoUtils.anyObject(), anyInt())).thenReturn(true)

        `when`(gameRepository.findByTournamentAndRoundAndNotVoided(tournament, game.playoff!!.round))
                .thenReturn(threeWayFinalGames)

        `when`(gameRepository.saveAll(MockitoUtils.anyObject<MutableIterable<Game>>()))
                .thenAnswer { answer ->
                    val games = answer.getArgument<MutableIterable<Game>>(0)
                    Assertions.assertThat(games).containsExactlyInAnyOrder(*threeWayFinalGames.toTypedArray())
                    Assertions.assertThat(games.map { it.voided }.any { !it }).isFalse()
                    games
                }
                .thenAnswer { answer ->
                    val games = answer.getArgument<MutableIterable<Game>>(0)
                    Assertions.assertThat(games.map { it.wasPlayed() }.any { it }).isFalse()
                    Assertions.assertThat(games.count { it.pairingInvolves(user1) }).isEqualTo(2)
                    Assertions.assertThat(games.count { it.pairingInvolves(user2) }).isEqualTo(2)
                    Assertions.assertThat(games.count { it.pairingInvolves(user3) }).isEqualTo(2)
                    Assertions.assertThat(games.any { it.homeUser == it.awayUser }).isFalse()
                    games
                }

        playoffService.advanceByGame(game)
    }

    private fun createOneWayFinalGames(): Pair<Game, Game> {
        val finalGame = EntityDefaults.game(
                homeUser = EntityDefaults.user(id = 1, username = "finalGameWinner"),
                awayUser = EntityDefaults.user(id = 2, username = "finalGameLoser"),
                scoreHome = 4,
                scoreAway = 1,
                playoff = PlayoffGame(round = 5, spot = 1),
                tournament = tournament)

        val thirdPlaceGame = finalGame.copy(
                homeUser = EntityDefaults.user(id = 3, username = "thirdPlaceGameLoser"),
                awayUser = EntityDefaults.user(id = 4, username = "thirdPlaceGameWinner"),
                scoreHome = 2,
                scoreAway = 4,
                playoff = PlayoffGame(round = finalGame.playoff!!.round - 1, spot = 1))

        return Pair(finalGame, thirdPlaceGame)
    }

}
