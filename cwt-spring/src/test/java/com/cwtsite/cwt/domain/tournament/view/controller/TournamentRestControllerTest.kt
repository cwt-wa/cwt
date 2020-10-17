package com.cwtsite.cwt.domain.tournament.view.controller

import com.cwtsite.cwt.domain.game.service.GameService
import com.cwtsite.cwt.domain.game.view.model.GameMinimalDto
import com.cwtsite.cwt.domain.group.service.GroupService
import com.cwtsite.cwt.domain.playoffs.service.PlayoffService
import com.cwtsite.cwt.domain.playoffs.service.TreeService
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.tournament.view.model.MapDto
import com.cwtsite.cwt.domain.user.service.UserService
import com.cwtsite.cwt.test.EntityDefaults
import org.assertj.core.api.Assertions.assertThat
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.sql.Timestamp
import java.util.*
import kotlin.test.Test

@RunWith(MockitoJUnitRunner::class)
class TournamentRestControllerTest {

    @InjectMocks
    private lateinit var cut: TournamentRestController

    @Mock
    private lateinit var tournamentService: TournamentService

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var groupService: GroupService

    @Mock
    private lateinit var playoffService: PlayoffService

    @Mock
    private lateinit var gameService: GameService

    @Mock
    private lateinit var treeService: TreeService

    @Test
    fun getMapsOfCurrentTournament() {
        val game = with(EntityDefaults.game()) {
            created = Timestamp(1602928837289)
            modified = Timestamp(1602928837289)
            this
        }
        val game1 = mapOf("texture" to "Level\\DATA\\Manhattan", "map" to "/map/tx3qwuc3")
        val game2 = mapOf("texture" to "Level\\DATA\\Hell", "map" to "/map/m7d7ys8y")
        `when`(gameService.findFromGameStats(game, "map", "texture"))
                .thenReturn(listOf(game1, game2))
        `when`(gameService.findAllOfTournament(game.tournament))
                .thenReturn(listOf(game))
        `when`(tournamentService.getTournament(game.tournament.id!!))
                .thenReturn(Optional.of(game.tournament))
        val actual = cut.getMapsOfCurrentTournament(game.tournament.id!!).body
        assertThat(actual).containsExactlyInAnyOrder(
                MapDto(game1["texture"], GameMinimalDto.toDto(game), game1.getValue("map")),
                MapDto(game2["texture"], GameMinimalDto.toDto(game), game2.getValue("map")))
    }
}
