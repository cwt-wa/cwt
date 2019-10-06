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
import org.junit.ComparisonFailure
import org.junit.Ignore
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.test.Test

@Ignore("Mocking has gotta be fixed.")
@RunWith(MockitoJUnitRunner::class)
class PlayoffServiceVoidGameTest {

    @InjectMocks private lateinit var playoffService: PlayoffService
    @Mock private lateinit var gameRepository: GameRepository
    @Mock private lateinit var configurationService: ConfigurationService
    @Mock private lateinit var groupRepository: GroupRepository
    @Mock private lateinit var tournamentService: TournamentService
    @Mock private lateinit var treeService: TreeService

    @Test
    fun `delete little final and final games when semifinal is to be voided`() {
        val tournament = EntityDefaults.tournament(status = TournamentStatus.PLAYOFFS)
        val playoffGames = createExamplePlayoffTree(tournament)
        val voidableGame = spy(playoffGames.find { it.id == 5L }!!)

        val spiedPlayoffService = spy(playoffService)

        `when`(treeService.getVoidablePlayoffGames()).thenReturn(listOf(voidableGame))

        `when`(gameRepository.findGameInPlayoffTree(MockitoUtils.anyObject<Tournament>(), anyInt(), anyInt()))
                .thenAnswer {
                    Optional.of(playoffGames.find { pG ->
                        pG.playoff!!.round == it.getArgument<Int>(1)
                                && pG.playoff!!.spot == it.getArgument<Int>(2)
                    }!!)
                }

        `when`(gameRepository.save(MockitoUtils.anyObject<Game>())).thenAnswer { it.getArgument<Game>(0) }

        `when`(gameRepository.delete(MockitoUtils.anyObject<Game>()))
                .thenAnswer {
                    val littleFinalGameToDelete = it.getArgument<Game>(0)
                    Assertions.assertThat(littleFinalGameToDelete.playoff!!.round).isEqualTo(3)
                    Assertions.assertThat(littleFinalGameToDelete.playoff!!.spot).isEqualTo(1)
                    littleFinalGameToDelete
                }
                .thenAnswer {
                    val finalGameToDelete = it.getArgument<Game>(0)
                    Assertions.assertThat(finalGameToDelete.playoff!!.round).isEqualTo(4)
                    Assertions.assertThat(finalGameToDelete.playoff!!.spot).isEqualTo(1)
                    finalGameToDelete
                }

        val replacementPlayoffGame = spiedPlayoffService.voidPlayoffGame(voidableGame)

        Assertions.assertThat(voidableGame.voided).isTrue()

        assertReplacementGame(replacementPlayoffGame, voidableGame)

        verify(gameRepository, times(2)).delete(MockitoUtils.anyObject<Game>())
        verify(gameRepository).findGameInPlayoffTree(tournament, 3, 1)
        verify(gameRepository).findGameInPlayoffTree(tournament, 4, 1)
    }

    @Test
    fun `delete affected three-way final games when semifinal game is to be voided`() {
        val spiedPlayoffService = spy(playoffService)
        val tournament = EntityDefaults.tournament(status = TournamentStatus.PLAYOFFS)
        val voidableGame = EntityDefaults.game(
                homeUser = EntityDefaults.user(id = 1, username = "Zemke"),
                awayUser = EntityDefaults.user(id = 2, username = "Koras"),
                scoreHome = 2,
                scoreAway = 3,
                playoff = PlayoffGame(id = 1, round = 1, spot = 2),
                tournament = tournament
        )

        `when`(treeService.getVoidablePlayoffGames()).thenReturn(listOf(voidableGame))

        val spiedList = spy(List::class.java); doReturn(3).`when`(spiedList).size
        doReturn(spiedList).`when`(gameRepository).findByTournamentAndRoundAndNotVoided(tournament, 1)

        val mockThreeWayFinalGame = { playoffId: Long, round: Int, spot: Int ->
            val gameMock = mock(Game::class.java)
            `when`(gameMock.playoff).thenReturn(PlayoffGame(id = playoffId, round = round, spot = spot))
            `when`(gameMock.homeUser).thenReturn(voidableGame.awayUser)
            gameMock
        }

        val threeWayFinalGames = listOf(mockThreeWayFinalGame(2, 2, 1), mockThreeWayFinalGame(3, 2, 2))

        `when`(gameRepository.findGameInPlayoffTree(voidableGame.tournament, voidableGame.awayUser, voidableGame.playoff!!.round + 1))
                .thenReturn(threeWayFinalGames)

        `when`(gameRepository.delete(MockitoUtils.anyObject<Game>()))
                .thenAnswer {
                    val gameToBeDeleted = it.getArgument<Game>(0)
                    Assertions.assertThat(gameToBeDeleted.playoff!!.round).isEqualTo(2)
                    try {
                        Assertions.assertThat(gameToBeDeleted.homeUser).isEqualTo(voidableGame.awayUser)
                    } catch (e: ComparisonFailure) {
                        Assertions.assertThat(gameToBeDeleted.awayUser).isEqualTo(voidableGame.awayUser)
                    }
                }

        `when`(gameRepository.save(MockitoUtils.anyObject<Game>())).thenAnswer { it.getArgument<Game>(0) }

        val replacementGame = spiedPlayoffService.voidPlayoffGame(voidableGame)
        assertReplacementGame(replacementGame, voidableGame)

        verify(gameRepository, times(2)).delete(MockitoUtils.anyObject<Game>())
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

    private fun createExamplePlayoffTree(tournament: Tournament): List<Game> {
        return listOf(
                EntityDefaults.game(
                        id = 1, tournament = tournament,
                        homeUser = EntityDefaults.user(id = 1),
                        awayUser = EntityDefaults.user(id = 2),
                        scoreHome = null, scoreAway = null,
                        playoff = PlayoffGame(id = 1, round = 1, spot = 1)
                ),
                EntityDefaults.game(
                        id = 2, tournament = tournament,
                        homeUser = EntityDefaults.user(id = 3),
                        awayUser = EntityDefaults.user(id = 4),
                        scoreHome = null, scoreAway = null,
                        playoff = PlayoffGame(id = 2, round = 1, spot = 2)
                ),
                EntityDefaults.game(
                        id = 3, tournament = tournament,
                        homeUser = EntityDefaults.user(id = 4),
                        awayUser = EntityDefaults.user(id = 5),
                        scoreHome = null, scoreAway = null,
                        playoff = PlayoffGame(id = 3, round = 1, spot = 3)
                ),
                EntityDefaults.game(
                        id = 4, tournament = tournament,
                        homeUser = EntityDefaults.user(id = 6),
                        awayUser = EntityDefaults.user(id = 7),
                        scoreHome = null, scoreAway = null,
                        playoff = PlayoffGame(id = 4, round = 1, spot = 4)
                ),
                EntityDefaults.game( // semifinal
                        id = 5, tournament = tournament,
                        homeUser = EntityDefaults.user(id = 4),
                        awayUser = EntityDefaults.user(id = 7),
                        scoreHome = 3, scoreAway = 0,
                        playoff = PlayoffGame(id = 5, round = 2, spot = 2)
                ),
                EntityDefaults.game( // little final
                        id = 6, tournament = tournament,
                        homeUser = null,
                        awayUser = EntityDefaults.user(id = 7),
                        scoreHome = null, scoreAway = null,
                        playoff = PlayoffGame(id = 6, round = 3, spot = 1)
                ),
                EntityDefaults.game( // final
                        id = 7, tournament = tournament,
                        homeUser = null,
                        awayUser = EntityDefaults.user(id = 4),
                        scoreHome = null, scoreAway = null,
                        playoff = PlayoffGame(id = 7, round = 4, spot = 1)
                )
        )
    }
}
