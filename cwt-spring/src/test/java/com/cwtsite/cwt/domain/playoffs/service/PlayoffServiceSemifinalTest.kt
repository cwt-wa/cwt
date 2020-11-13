package com.cwtsite.cwt.domain.playoffs.service

import com.cwtsite.cwt.domain.configuration.entity.Configuration
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.configuration.service.ConfigurationRepository
import com.cwtsite.cwt.domain.configuration.service.ConfigurationService
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.PlayoffGame
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.group.service.GroupRepository
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.test.EntityDefaults
import com.cwtsite.cwt.test.MockitoUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class PlayoffServiceSemifinalTest {

    @InjectMocks
    private lateinit var playoffService: PlayoffService

    @Mock
    private lateinit var gameRepository: GameRepository

    @Mock
    private lateinit var configurationService: ConfigurationService

    @Mock
    private lateinit var configurationRepository: ConfigurationRepository

    @Mock
    private lateinit var groupRepository: GroupRepository

    @Mock
    private lateinit var tournamentService: TournamentService

    @Mock
    private lateinit var treeService: TreeService

    private val tournament = EntityDefaults.tournament(status = TournamentStatus.PLAYOFFS, maxRounds = 2)

    @Before
    fun initMocks() {
        `when`(treeService.isThreeWayFinalGame(MockitoUtils.anyObject(), anyInt())).thenReturn(false)
        `when`(treeService.isThirdPlaceGame(MockitoUtils.anyObject(), anyInt())).thenReturn(false)
        `when`(treeService.isFinalGame(MockitoUtils.anyObject(), anyInt())).thenReturn(false)
        `when`(treeService.isSemifinalGame(MockitoUtils.anyObject(), anyInt())).thenReturn(true)
        `when`(gameRepository.save(MockitoUtils.anyObject<Game>())).thenAnswer { it.getArgument<Game>(0) }
        `when`(treeService.nextPlayoffSpotForOneWayFinalTree(anyInt(), anyInt())).thenCallRealMethod()
    }

    @Test
    fun advanceByGame_finalsToNotYetExist() {
        val winner = EntityDefaults.user()
        val loser = EntityDefaults.user(id = 2)

        val reportedGame = Game(
                scoreHome = 3,
                scoreAway = 1,
                homeUser = winner,
                awayUser = loser,
                playoff = PlayoffGame(round = 3, spot = 2),
                tournament = tournament
        )

        val changedOrUpdatedGames = playoffService.advanceByGame(reportedGame)

        assertThat(changedOrUpdatedGames).hasSize(2)

        val final = changedOrUpdatedGames.find { it.playoff!!.round == 5 }!!
        val littleFinal = changedOrUpdatedGames.find { it.playoff!!.round == 4 }!!

        assertThat(final.homeUser).isNull()
        assertThat(final.awayUser).isEqualTo(winner)

        assertThat(littleFinal.homeUser).isNull()
        assertThat(littleFinal.awayUser).isEqualTo(loser)
    }

    @Test
    fun advanceByGame_finalsAlreadyExist() {
        val winner = EntityDefaults.user()
        val loser = EntityDefaults.user(id = 2)
        val existingFinalist = EntityDefaults.user(id = 3)
        val existingLittleFinalist = EntityDefaults.user(id = 4)

        val reportedGame = Game(
                scoreHome = 3,
                scoreAway = 1,
                homeUser = winner,
                awayUser = loser,
                playoff = PlayoffGame(round = 3, spot = 2),
                tournament = tournament
        )

        val existingLittleFinal = Game(
                scoreHome = null,
                scoreAway = null,
                homeUser = existingLittleFinalist,
                awayUser = null,
                playoff = PlayoffGame(round = 4, spot = 1),
                tournament = tournament
        )

        val existingFinal = Game(
                scoreHome = null,
                scoreAway = null,
                homeUser = existingFinalist,
                awayUser = null,
                playoff = PlayoffGame(round = 5, spot = 1),
                tournament = tournament
        )

        `when`(gameRepository.findGameInPlayoffTree(tournament, existingLittleFinal.playoff!!.round, existingLittleFinal.playoff!!.spot))
                .thenReturn(Optional.of(existingLittleFinal))

        `when`(gameRepository.findGameInPlayoffTree(tournament, existingFinal.playoff!!.round, existingFinal.playoff!!.spot))
                .thenReturn(Optional.of(existingFinal))

        val changedOrUpdatedGames = playoffService.advanceByGame(reportedGame)

        assertThat(changedOrUpdatedGames).hasSize(2)

        val final = changedOrUpdatedGames.find { it.playoff!!.round == 5 }!!
        val littleFinal = changedOrUpdatedGames.find { it.playoff!!.round == 4 }!!


        assertThat(final.homeUser).isEqualTo(existingFinalist)
        assertThat(final.awayUser).isEqualTo(winner)

        assertThat(littleFinal.homeUser).isEqualTo(existingLittleFinalist)
        assertThat(littleFinal.awayUser).isEqualTo(loser)
    }
}
