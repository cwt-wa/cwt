package com.cwtsite.cwt.domain.playoffs.service

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
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.test.Test

@RunWith(MockitoJUnitRunner::class)
class PlayoffServiceVoidGameTest {

    @InjectMocks private lateinit var playoffService: PlayoffService
    @Mock private lateinit var gameRepository: GameRepository
    @Mock private lateinit var configurationService: ConfigurationService
    @Mock private lateinit var groupRepository: GroupRepository
    @Mock private lateinit var tournamentService: TournamentService
    @Mock private lateinit var treeService: TreeService

    @Test
    fun `delete one-way final games when semifinal is voided`() {
        val tournament = EntityDefaults.tournament(status = TournamentStatus.PLAYOFFS)
        val game = EntityDefaults.game( // semifinal game to be voided
                id = 5, tournament = tournament,
                homeUser = EntityDefaults.user(id = 4),
                awayUser = EntityDefaults.user(id = 7),
                scoreHome = 3, scoreAway = 0,
                playoff = PlayoffGame(id = 5, round = 2, spot = 2)
        )
        val littleFinal = EntityDefaults.game(
                id = 2,
                homeUser = game.loser(),
                awayUser = null,
                playoff = PlayoffGame(id = 2, round = game.playoff!!.round + 1, spot = 1)
        )
        val final = EntityDefaults.game(
                id = 2,
                homeUser = game.winner(),
                awayUser = null,
                playoff = PlayoffGame(id = 2, round = littleFinal.playoff!!.round + 1, spot = 1)
        )

        `when`(treeService.getVoidablePlayoffGames())
                .thenReturn(listOf(game))

        `when`(treeService.isSomeKindOfFinalGame(game))
                .thenReturn(false)

        `when`(treeService.nextPlayoffSpotForOneWayFinalTree(game.playoff!!.round, game.playoff!!.spot))
                .thenReturn(Pair(game.playoff!!.round + 1, 1))

        `when`(treeService.isThreeWayFinalGame(game.tournament, game.playoff!!.round))
                .thenReturn(false)

        `when`(gameRepository.findGameInPlayoffTree(MockitoUtils.anyObject<Tournament>(), anyInt(), anyInt()))
                .thenReturn(Optional.of(littleFinal))

        `when`(treeService.isThirdPlaceGame(littleFinal.tournament, littleFinal.playoff!!.round))
                .thenReturn(true)

        `when`(gameRepository.findGameInPlayoffTree(littleFinal.tournament, littleFinal.playoff!!.round + 1, 1))
                .thenReturn(Optional.of(final))

        `when`(gameRepository.save(MockitoUtils.anyObject<Game>()))
                .thenAnswer { it.getArgument<Game>(0) }

        `when`(gameRepository.delete(MockitoUtils.anyObject()))
                .thenAnswer { Assertions.assertThat(it.getArgument<Game>(0)).isEqualTo(littleFinal) }
                .thenAnswer { Assertions.assertThat(it.getArgument<Game>(0)).isEqualTo(final) }

        val replacementPlayoffGame = playoffService.voidPlayoffGame(game)
        assertReplacementGame(replacementPlayoffGame, game)
        verify(gameRepository, times(2)).delete(MockitoUtils.anyObject<Game>())
        Assertions.assertThat(game.voided).isTrue()
    }

    @Test
    fun `delete affected three-way final games when semifinal game is voided`() {
        val game = Game(
                id = 1,
                tournament = EntityDefaults.tournament(status = TournamentStatus.PLAYOFFS),
                homeUser = EntityDefaults.user(),
                awayUser = EntityDefaults.user(id = 2, username = "Kayz"),
                scoreHome = 3,
                scoreAway = 2,
                playoff = PlayoffGame(id = 1, round = 1, spot = 2)
        )
        val threeWayFinals = listOf(
                Game(
                        id = 2,
                        tournament = EntityDefaults.tournament(status = TournamentStatus.PLAYOFFS),
                        homeUser = game.winner(),
                        awayUser = null,
                        playoff = PlayoffGame(id = 1, round = 3, spot = 1)
                ),
                Game(
                        id = 3,
                        tournament = EntityDefaults.tournament(status = TournamentStatus.PLAYOFFS),
                        homeUser = game.winner(),
                        awayUser = null,
                        playoff = PlayoffGame(id = 1, round = 3, spot = 2)
                ),
                Game(
                        id = 4,
                        tournament = EntityDefaults.tournament(status = TournamentStatus.PLAYOFFS),
                        homeUser = null,
                        awayUser = null,
                        playoff = PlayoffGame(id = 1, round = 3, spot = 3)
                )
        )


        `when`(treeService.getVoidablePlayoffGames())
                .thenReturn(listOf(game))

        `when`(treeService.isSomeKindOfFinalGame(game))
                .thenReturn(false)

        `when`(treeService.nextPlayoffSpotForOneWayFinalTree(game.playoff!!.round, game.playoff!!.spot))
                .thenReturn(Pair(game.playoff!!.round + 1, 1))

        `when`(treeService.isThreeWayFinalGame(game.tournament, game.playoff!!.round + 1))
                .thenReturn(true)

        `when`(gameRepository.findGameInPlayoffTree(game.tournament, game.winner(), game.playoff!!.round + 1))
                .thenReturn(threeWayFinals)

        `when`(gameRepository.delete(MockitoUtils.anyObject()))
                .thenAnswer { Assertions.assertThat(it.getArgument<Game>(0)).isEqualTo(threeWayFinals[0]) }
                .thenAnswer { Assertions.assertThat(it.getArgument<Game>(0)).isEqualTo(threeWayFinals[1]) }
                .thenAnswer { Assertions.assertThat(it.getArgument<Game>(0)).isEqualTo(threeWayFinals[2]) }

        `when`(gameRepository.save(MockitoUtils.anyObject<Game>()))
                .thenAnswer { it.getArgument<Game>(0) }

        val replacementGame = playoffService.voidPlayoffGame(game)
        assertReplacementGame(replacementGame, game)
        verify(gameRepository).delete(threeWayFinals.find { it.pairingInvolves(game.winner()) }!!)
        verify(gameRepository).delete(threeWayFinals.findLast { it.pairingInvolves(game.winner()) }!!)
        Assertions.assertThat(game.voided).isTrue()
    }

    @Test
    fun `update affected three-way finals when semifinal is voided`() {
        TODO()
    }

    @Test
    fun `delete game to advance to when it's not a final`() {
        TODO()
    }

    @Test
    fun `delete game no game when there's none to advance to`() {
        TODO()
    }

    private fun assertReplacementGame(replacementPlayoffGame: Game, voidableGame: Game) {
        Assertions.assertThat(replacementPlayoffGame.id).isNull()
        Assertions.assertThat(replacementPlayoffGame.homeUser).isEqualTo(voidableGame.homeUser)
        Assertions.assertThat(replacementPlayoffGame.awayUser).isEqualTo(voidableGame.awayUser)
        Assertions.assertThat(replacementPlayoffGame.tournament).isEqualTo(voidableGame.tournament)

        Assertions.assertThat(replacementPlayoffGame.playoff!!.spot).isEqualTo(voidableGame.playoff!!.spot)
        Assertions.assertThat(replacementPlayoffGame.playoff!!.round).isEqualTo(voidableGame.playoff!!.round)
        Assertions.assertThat(replacementPlayoffGame.playoff!!.id).isNull()

        Assertions.assertThat(replacementPlayoffGame.reporter).isNull()
        Assertions.assertThat(replacementPlayoffGame.voided).isFalse()
        Assertions.assertThat(replacementPlayoffGame.created).isNull()
    }
}
