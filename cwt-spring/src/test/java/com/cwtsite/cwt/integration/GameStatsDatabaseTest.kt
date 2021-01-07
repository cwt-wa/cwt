package com.cwtsite.cwt.integration

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.GameStats
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.game.service.GameService
import com.cwtsite.cwt.domain.game.service.GameStatsRepository
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.BufferedReader
import java.time.Instant


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EmbeddedPostgres
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class GameStatsDatabaseTest {

    @Autowired private lateinit var gameService: GameService
    @Autowired private lateinit var gameRepository: GameRepository
    @Autowired private lateinit var gameStatsRepository: GameStatsRepository
    @Autowired private lateinit var tournamentRepository: TournamentRepository

    private val statsJson =
            javaClass.getResourceAsStream("/com/cwtsite/cwt/integration/1513/2.json")!!
                    .bufferedReader().use(BufferedReader::readText)

    companion object {

        @JvmStatic private var game: Game? = null
    }

    @Test
    @Order(1)
    fun `save game`() {
        game = gameRepository.save(Game(
                tournament = tournamentRepository.save(
                        Tournament(created = Instant.now()))))
    }

    @Test
    @Order(2)
    fun `save stats`() {
        gameStatsRepository.save(GameStats(
                game = game,
                startedAt = Instant.ofEpochMilli(1586284441226),
                data = statsJson))
    }

    @Test
    @Order(2)
    fun `query stats`() {
        assertThat(gameService.findGameStats(gameService.findById(game!!.id!!).orElseThrow()))
                .isEqualTo("[$statsJson]")
    }
}
