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
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
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
        val game = createVoidableGame() // semifinal game to be voided
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
                .thenReturn(listOf(game, final, littleFinal))

        `when`(treeService.isSomeKindOfFinalGame(game))
                .thenReturn(false)

        `when`(treeService.nextPlayoffSpotForOneWayFinalTree(game.playoff!!.round, game.playoff!!.spot))
                .thenReturn(game.playoff!!.round + 1 to 1)

        `when`(treeService.isThreeWayFinalGame(game.tournament, game.playoff!!.round + 1))
                .thenReturn(false)

        `when`(gameRepository.findGameInPlayoffTree(MockitoUtils.anyObject<Tournament>(), anyInt(), anyInt()))
                .thenReturn(Optional.of(littleFinal))

        `when`(treeService.isThirdPlaceGame(littleFinal.tournament, littleFinal.playoff!!.round))
                .thenReturn(true)

        `when`(gameRepository.findGameInPlayoffTree(littleFinal.tournament, littleFinal.playoff!!.round + 1, 1))
                .thenReturn(Optional.of(final))

        `when`(gameRepository.save(MockitoUtils.anyObject<Game>()))
                .thenAnswer { it.getArgument<Game>(0) }

        val replacementPlayoffGame = playoffService.voidPlayoffGame(game)
        assertReplacementGame(replacementPlayoffGame, game)
        verify(treeService).removePartOfPlayoffGame(final, game.winner(), false)
        verify(treeService).removePartOfPlayoffGame(littleFinal, game.loser(), false)
    }

    @Test
    fun `delete affected three-way final games when semifinal game is voided`() {
        val game = createVoidableGame()
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
                .thenReturn(listOf(game, *threeWayFinals.toTypedArray()))

        `when`(treeService.isSomeKindOfFinalGame(game))
                .thenReturn(false)

        `when`(treeService.nextPlayoffSpotForOneWayFinalTree(game.playoff!!.round, game.playoff!!.spot))
                .thenReturn(game.playoff!!.round + 1 to 1)

        `when`(treeService.isThreeWayFinalGame(game.tournament, game.playoff!!.round + 1))
                .thenReturn(true)

        `when`(gameRepository.findGameInPlayoffTree(game.tournament, game.winner(), game.playoff!!.round + 1))
                .thenReturn(threeWayFinals)

        `when`(gameRepository.save(MockitoUtils.anyObject<Game>()))
                .thenAnswer { it.getArgument<Game>(0) }

        val replacementGame = playoffService.voidPlayoffGame(game)
        assertReplacementGame(replacementGame, game)
        verify(treeService).removePartOfPlayoffGame(threeWayFinals[0], game.winner(), true)
        verify(treeService).removePartOfPlayoffGame(threeWayFinals[1], game.winner(), true)
    }

    @Test
    fun `delete game to advance to when it's not a final`() {
        val game = createVoidableGame()
        val gameToAdvanceTo = Game(
                id = 2,
                homeUser = EntityDefaults.user(id = 20, username = "AdvanceGameOpponent"),
                awayUser = game.winner(),
                scoreHome = null,
                scoreAway = null,
                reporter = null,
                playoff = PlayoffGame(id = 99, round = game.playoff!!.round + 1, spot = 1),
                tournament = game.tournament
        )
        `when`(treeService.getVoidablePlayoffGames()).thenReturn(listOf(game))
        `when`(treeService.isSomeKindOfFinalGame(game)).thenReturn(false)
        `when`(treeService.nextPlayoffSpotForOneWayFinalTree(game.playoff!!.round, game.playoff!!.spot))
                .thenReturn(2 to 1)
        `when`(treeService.isThreeWayFinalGame(game.tournament, game.playoff!!.round + 1)).thenReturn(false)
        `when`(gameRepository.findGameInPlayoffTree(game.tournament, game.playoff!!.round + 1, 1))
                .thenReturn(Optional.of(gameToAdvanceTo))
        `when`(gameRepository.save(MockitoUtils.anyObject<Game>())).thenAnswer { it.getArgument<Game>(0) }

        val replacementGame = playoffService.voidPlayoffGame(game)
        assertReplacementGame(replacementGame, game)
        verify(treeService).removePartOfPlayoffGame(gameToAdvanceTo, game.winner(), false)
    }

    @Test
    fun `delete no game when there's none to advance to`() {
        val game = createVoidableGame()
        `when`(treeService.getVoidablePlayoffGames()).thenReturn(listOf(game))
        `when`(treeService.isSomeKindOfFinalGame(game)).thenReturn(true)
        verify(treeService, never()).removePartOfPlayoffGame(MockitoUtils.anyObject(), MockitoUtils.anyObject(), anyBoolean())
        `when`(gameRepository.save(MockitoUtils.anyObject<Game>())).thenAnswer { it.getArgument<Game>(0) }
        val replacementGame = playoffService.voidPlayoffGame(game)
        assertReplacementGame(replacementGame, game)
    }

    @Test
    fun `crash when voiding a playoff game which is not voidable`() {
        `when`(treeService.getVoidablePlayoffGames()).thenReturn(emptyList())
        assertThatThrownBy { playoffService.voidPlayoffGame(mock(Game::class.java)) }
                .isInstanceOf(PlayoffService.PlayoffGameNotVoidableException::class.java)
    }

    private fun createVoidableGame(): Game {
        return Game(
                id = 1,
                tournament = EntityDefaults.tournament(status = TournamentStatus.PLAYOFFS),
                homeUser = EntityDefaults.user(),
                awayUser = EntityDefaults.user(id = 2, username = "Kayz"),
                scoreHome = 3,
                scoreAway = 2,
                playoff = PlayoffGame(id = 1, round = 1, spot = 2)
        )
    }

    private fun assertReplacementGame(replacementPlayoffGame: Game, voidableGame: Game) {
        assertThat(voidableGame.voided).isTrue()

        assertThat(replacementPlayoffGame.id).isNull()
        assertThat(replacementPlayoffGame.homeUser).isEqualTo(voidableGame.homeUser)
        assertThat(replacementPlayoffGame.awayUser).isEqualTo(voidableGame.awayUser)
        assertThat(replacementPlayoffGame.tournament).isEqualTo(voidableGame.tournament)

        assertThat(replacementPlayoffGame.playoff!!.spot).isEqualTo(voidableGame.playoff!!.spot)
        assertThat(replacementPlayoffGame.playoff!!.round).isEqualTo(voidableGame.playoff!!.round)
        assertThat(replacementPlayoffGame.playoff!!.id).isNull()

        assertThat(replacementPlayoffGame.reporter).isNull()
        assertThat(replacementPlayoffGame.voided).isFalse()
        assertThat(replacementPlayoffGame.created).isNull()
    }
}
