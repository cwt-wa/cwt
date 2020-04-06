package com.cwtsite.cwt.integration

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.GameStats
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.game.service.GameService
import com.cwtsite.cwt.domain.game.service.GameStatsRepository
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.FixMethodOrder
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.nio.charset.Charset
import java.sql.Timestamp
import java.time.Instant
import kotlin.test.Test


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EmbeddedPostgres
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class GameStatsDatabaseIntegration {

    @Autowired
    private lateinit var gameService: GameService

    @Autowired
    private lateinit var gameRepository: GameRepository

    @Autowired
    private lateinit var gameStatsRepository: GameStatsRepository

    @Autowired
    private lateinit var tournamentRepository: TournamentRepository

    private val statsJson = javaClass.getResource("stats.json")!!
            .readBytes().toString(Charset.defaultCharset())

    companion object {

        @JvmStatic
        private var game: Game? = null
    }

    @Test
    fun `1 save game`() {
        game = gameRepository.save(Game(
                tournament = tournamentRepository.save(
                        Tournament(created = Timestamp.from(Instant.now())))))
    }

    @Test
    fun `2 save stats`() {
        gameStatsRepository.save(GameStats(
                gameId = game!!.id!!,
                game = game,
                round = 1,
                data = statsJson))
    }

    @Test
    fun `3 query stats`() {
        assertThat(gameService.findGameStats(gameService.findById(game!!.id!!).orElseThrow()))
                .isEqualTo("[$statsJson]")
    }
}
