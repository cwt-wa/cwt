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
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.test.Test

@RunWith(MockitoJUnitRunner::class)
class PlayoffServiceVoidGameTest {

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

    @Test
    fun `delete little final and final games when semifinal is to be voided`() {
        val tournament = EntityDefaults.tournament(status = TournamentStatus.PLAYOFFS)
        val playoffGames = createExamplePlayoffTree(tournament)
        val voidableGame = Mockito.spy(playoffGames.find { it.id == 5L }!!)

        val spiedPlayoffService = Mockito.spy(playoffService)

        Mockito
                .doReturn(listOf(voidableGame))
                .`when`(spiedPlayoffService).getVoidableGames()

        mockNumberOfPlayoffRoundsInTournament(spiedPlayoffService, tournament, 3)

        Mockito
                .`when`(gameRepository.findGameInPlayoffTree(MockitoUtils.anyObject<Tournament>(), Mockito.anyInt(), Mockito.anyInt()))
                .thenAnswer {
                    Optional.of(playoffGames.find { pG ->
                        pG.playoff!!.round == it.getArgument<Int>(1)
                                && pG.playoff!!.spot == it.getArgument<Int>(2)
                    }!!)
                }

        Mockito
                .`when`(gameRepository.save(MockitoUtils.anyObject<Game>()))
                .thenAnswer { it.getArgument<Game>(0) }

        Mockito
                .`when`(gameRepository.delete(MockitoUtils.anyObject<Game>()))
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

        Mockito.verify(gameRepository, Mockito.times(2)).delete(MockitoUtils.anyObject<Game>())
        Mockito.verify(gameRepository).findGameInPlayoffTree(tournament, 3, 1)
        Mockito.verify(gameRepository).findGameInPlayoffTree(tournament, 4, 1)
    }

    private fun mockNumberOfPlayoffRoundsInTournament(spiedPlayoffService: PlayoffService, tournament: Tournament, value: Int) {
        Mockito
                .doReturn(value)
                .`when`(spiedPlayoffService).getNumberOfPlayoffRoundsInTournament(tournament)
    }

    @Test
    fun `delete affected three-way final games when semifinal game is to be voided`() {
        val spiedPlayoffService = Mockito.spy(playoffService)
        val tournament = EntityDefaults.tournament(status = TournamentStatus.PLAYOFFS)
        val voidableGame = EntityDefaults.game(
                homeUser = EntityDefaults.user(id = 1, username = "Zemke"),
                awayUser = EntityDefaults.user(id = 2, username = "Koras"),
                scoreHome = 2,
                scoreAway = 3,
                playoff = PlayoffGame(id = 1, round = 1, spot = 2),
                tournament = tournament
        )

        Mockito
                .doReturn(listOf(voidableGame))
                .`when`(spiedPlayoffService).getVoidableGames()

        mockNumberOfPlayoffRoundsInTournament(spiedPlayoffService, tournament, 2)


        val spiedList = Mockito.spy(List::class.java); Mockito.doReturn(3).`when`(spiedList).size
        Mockito
                .doReturn(spiedList)
                .`when`(gameRepository).findByTournamentAndRoundAndNotVoided(tournament, 1)

        val mockThreeWayFinalGame = { playoffId: Long, round: Int, spot: Int ->
            val gameMock = Mockito.mock(Game::class.java)
            Mockito.`when`(gameMock.playoff).thenReturn(PlayoffGame(id = playoffId, round = round, spot = spot))
            gameMock
        }

        val threeWayFinalGames = listOf(mockThreeWayFinalGame(2, 2, 1), mockThreeWayFinalGame(3, 2, 2))

        Mockito
                .`when`(gameRepository.findGameInPlayoffTree(voidableGame.tournament, voidableGame.awayUser, voidableGame.playoff!!.round + 1))
                .thenReturn(threeWayFinalGames)

        Mockito
                .`when`(gameRepository.save(MockitoUtils.anyObject<Game>()))
                .thenAnswer { it.getArgument<Game>(0) }

        val replacementGame = spiedPlayoffService.voidPlayoffGame(voidableGame)

        assertReplacementGame(replacementGame, voidableGame)

        Mockito.verify(gameRepository).findGameInPlayoffTree(threeWayFinalGames[0].tournament, voidableGame.awayUser, 2)
        Mockito.verify(gameRepository).findGameInPlayoffTree(threeWayFinalGames[1].tournament, voidableGame.awayUser, 2)
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
