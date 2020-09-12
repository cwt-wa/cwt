package com.cwtsite.cwt.domain.playoffs.service

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.PlayoffGame
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.group.service.GroupRepository
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.test.EntityDefaults
import com.cwtsite.cwt.test.MockitoUtils
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Assert
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.test.Test

@RunWith(MockitoJUnitRunner::class)
class TreeServiceTest {

    @InjectMocks
    private lateinit var treeService: TreeService

    @Mock
    private lateinit var gameRepository: GameRepository

    @Mock
    private lateinit var tournamentService: TournamentService

    @Mock
    private lateinit var groupRepository: GroupRepository

    @Test
    fun getVoidableGames() {
        val tournament = EntityDefaults.tournament(status = TournamentStatus.PLAYOFFS)

        `when`(tournamentService.getCurrentTournament())
                .thenReturn(tournament)

        val allPlayoffGames = listOf(
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

        `when`(gameRepository.findByTournamentAndPlayoffIsNotNull(tournament))
                .thenReturn(allPlayoffGames)

        `when`(gameRepository.findGameInPlayoffTree(MockitoUtils.anyObject(), anyInt(), anyInt()))
                .thenAnswer {
                    val round = it.getArgument<Int>(1)
                    val spot = it.getArgument<Int>(2)
                    Optional.of(allPlayoffGames.find { pG -> pG.playoff!!.round == round && pG.playoff!!.spot == spot }!!)
                }

        assertThat(treeService.getVoidablePlayoffGames())
                .containsExactlyInAnyOrder(allPlayoffGames.find { it.id == 5L }!!)
    }

    @Test
    fun getNumberOfPlayoffRoundsInTournament() {
        val tournament = EntityDefaults.tournament()

        `when`(groupRepository.countByTournament(tournament))
                .thenReturn(4 / 2)
                .thenReturn(6 / 2)
                .thenReturn(8 / 2)
                .thenReturn(12 / 2)
                .thenReturn(16 / 2)
                .thenReturn(24 / 2)
                .thenReturn(32 / 2)
                .thenReturn(48 / 2)
                .thenReturn(64 / 2)
                .thenReturn(96 / 2)
                .thenReturn(128 / 2)

        assertThat(treeService.getNumberOfPlayoffRoundsInTournament(tournament)).isEqualTo(2)
        assertThat(treeService.getNumberOfPlayoffRoundsInTournament(tournament)).isEqualTo(2)
        assertThat(treeService.getNumberOfPlayoffRoundsInTournament(tournament)).isEqualTo(3)
        assertThat(treeService.getNumberOfPlayoffRoundsInTournament(tournament)).isEqualTo(3)
        assertThat(treeService.getNumberOfPlayoffRoundsInTournament(tournament)).isEqualTo(4)
        assertThat(treeService.getNumberOfPlayoffRoundsInTournament(tournament)).isEqualTo(4)
        assertThat(treeService.getNumberOfPlayoffRoundsInTournament(tournament)).isEqualTo(5)
        assertThat(treeService.getNumberOfPlayoffRoundsInTournament(tournament)).isEqualTo(5)
        assertThat(treeService.getNumberOfPlayoffRoundsInTournament(tournament)).isEqualTo(6)
        assertThat(treeService.getNumberOfPlayoffRoundsInTournament(tournament)).isEqualTo(6)
        assertThat(treeService.getNumberOfPlayoffRoundsInTournament(tournament)).isEqualTo(7)
    }

    @Test
    fun onlyFinalGamesAreLeftToPlay() {
        `when`(groupRepository.countByTournament(MockitoUtils.anyObject<Tournament>()))
                .thenReturn(8)

        val mockListSize = { listSize: Int ->
            val list = mock(List::class.java)
            `when`(list.size).thenReturn(listSize)
            list
        }

        val groupTournament = EntityDefaults.tournament(id = 1, status = TournamentStatus.GROUP)
        val playoffTournament = EntityDefaults.tournament(id = 2, status = TournamentStatus.PLAYOFFS)

        `when`(tournamentService.getCurrentTournament())
                .thenReturn(groupTournament)
                .thenReturn(playoffTournament)

        `when`(gameRepository.findReadyGamesInRoundEqualOrGreaterThan(4, playoffTournament))
                .thenAnswer { mockListSize(2) }
                .thenAnswer { mockListSize(0) }

        Assert.assertFalse(treeService.onlyFinalGamesAreLeftToPlay())
        Assert.assertTrue(treeService.onlyFinalGamesAreLeftToPlay())
        Assert.assertFalse(treeService.onlyFinalGamesAreLeftToPlay())
    }

    @Test
    fun isPlayoffTreeWithThreeWayFinal() {
        val tournament = EntityDefaults.tournament()
        val fn = { listSize: Int ->
            `when`(mock(List::class.java).size)
                    .thenReturn(listSize)
                    .getMock<List<Any>>()
        }

        `when`(gameRepository.findByTournamentAndRoundAndNotVoided(tournament, 1))
                .thenAnswer { fn(4 / 2) }
                .thenAnswer { fn(6 / 2) }
                .thenAnswer { fn(8 / 2) }
                .thenAnswer { fn(12 / 2) }
                .thenAnswer { fn(16 / 2) }
                .thenAnswer { fn(24 / 2) }
                .thenAnswer { fn(32 / 2) }
                .thenAnswer { fn(48 / 2) }
                .thenAnswer { fn(64 / 2) }
                .thenAnswer { fn(96 / 2) }
                .thenAnswer { fn(128 / 2) }

        assertThat(treeService.isPlayoffTreeWithThreeWayFinal(tournament)).isFalse()
        assertThat(treeService.isPlayoffTreeWithThreeWayFinal(tournament)).isTrue()
        assertThat(treeService.isPlayoffTreeWithThreeWayFinal(tournament)).isFalse()
        assertThat(treeService.isPlayoffTreeWithThreeWayFinal(tournament)).isTrue()
        assertThat(treeService.isPlayoffTreeWithThreeWayFinal(tournament)).isFalse()
        assertThat(treeService.isPlayoffTreeWithThreeWayFinal(tournament)).isTrue()
        assertThat(treeService.isPlayoffTreeWithThreeWayFinal(tournament)).isFalse()
        assertThat(treeService.isPlayoffTreeWithThreeWayFinal(tournament)).isTrue()
        assertThat(treeService.isPlayoffTreeWithThreeWayFinal(tournament)).isFalse()
        assertThat(treeService.isPlayoffTreeWithThreeWayFinal(tournament)).isTrue()
        assertThat(treeService.isPlayoffTreeWithThreeWayFinal(tournament)).isFalse()
    }

    @Test
    fun nextPlayoffSpotForOneWayFinalTree() {
        assertThat(treeService.nextPlayoffSpotForOneWayFinalTree(1, 6)).isEqualTo(Pair(2, 3))
        assertThat(treeService.nextPlayoffSpotForOneWayFinalTree(3, 1)).isEqualTo(Pair(4, 1))
        assertThat(treeService.nextPlayoffSpotForOneWayFinalTree(2, 2)).isEqualTo(Pair(3, 1))
        assertThat(treeService.nextPlayoffSpotForOneWayFinalTree(2, 4)).isEqualTo(Pair(3, 2))
    }

    @Test
    fun removePartOfPlayoffGame_alreadyPlayed() {
        val user = mock(User::class.java)
        val game = mock(Game::class.java)
        `when`(game.pairingInvolves(user)).thenReturn(true)
        `when`(game.wasPlayed()).thenReturn(true)
        assertThatThrownBy { treeService.removePartOfPlayoffGame(game, user, false) }
                .isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun removePartOfPlayoffGame_userNotInvolved() {
        val user = mock(User::class.java)
        val game = mock(Game::class.java)
        `when`(game.pairingInvolves(user)).thenReturn(false)
        assertThatThrownBy { treeService.removePartOfPlayoffGame(game, user, false) }
                .isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun removePartOfPlayoffGame_halfPaired() {
        val user = mock(User::class.java)
        val game = mock(Game::class.java)
        `when`(game.pairingInvolves(user)).thenReturn(true)
        `when`(game.pairingCompleted()).thenReturn(false)
        `when`(game.pairingHalfCompleted()).thenReturn(true)
        assertThat(treeService.removePartOfPlayoffGame(game, user, false)).isNull()
        verify(gameRepository).delete(game)
    }

    @Test
    fun removePartOfPlayoffGame_pairingCompleted() {
        val user = mock(User::class.java)
        val otherUser = mock(User::class.java)
        val game = spy(Game::class.java)
        game.homeUser = otherUser
        game.awayUser = user
        `when`(game.pairingCompleted()).thenReturn(true)
        `when`(game.pairingInvolves(user)).thenReturn(true)
        val partRemovedGame = treeService.removePartOfPlayoffGame(game, user, false)
        assertThat(partRemovedGame!!.homeUser).isNotNull()
        assertThat(partRemovedGame.awayUser).isNull()
        verify(gameRepository, never()).delete(game)
    }

    @Test
    fun removePartOfPlayoffGame_threeWayFinal() {
        val user = mock(User::class.java)
        val otherUser = mock(User::class.java)
        val game = spy(Game::class.java)
        game.homeUser = otherUser
        game.awayUser = user
        val partRemovedGame = treeService.removePartOfPlayoffGame(game, user, true)
        assertThat(partRemovedGame!!.homeUser).isNotNull()
        assertThat(partRemovedGame.awayUser).isNull()
        verify(gameRepository, never()).delete(game)
    }
}
