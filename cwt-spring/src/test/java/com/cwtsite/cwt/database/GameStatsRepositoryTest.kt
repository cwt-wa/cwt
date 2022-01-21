package com.cwtsite.cwt.database

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.GameStats
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.game.service.GameStatsRepository
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository
import com.cwtsite.cwt.integration.EmbeddedPostgres
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import java.time.Instant

@RunWith(SpringRunner::class)
@DataJpaTest
@EmbeddedPostgres
class GameStatsRepositoryTest : AbstractDatabaseTest() {

    @Autowired private lateinit var cut: GameStatsRepository
    @Autowired private lateinit var gameRepository: GameRepository
    @Autowired private lateinit var tournamentRepository: TournamentRepository

    @Test
    fun findTextureDistinct() {
        val tournament = tournamentRepository.save(Tournament())
        val game = gameRepository.save(Game(tournament = tournament))
        cut.save(GameStats(data = "", game = game, map = "/map/asdf", startedAt = Instant.ofEpochMilli(1577833200001), texture = "Data\\Level\\Hell"))
        cut.save(GameStats(data = "", game = game, map = "/map/asdf", startedAt = Instant.ofEpochMilli(1577833200002), texture = "Data\\Level\\Hell"))
        cut.save(GameStats(data = "", game = game, map = "/map/asdf", startedAt = Instant.ofEpochMilli(1577833200003), texture = "Data\\Level\\Tentacles"))
        cut.save(GameStats(data = "", game = game, map = "/map/asdf", startedAt = Instant.ofEpochMilli(1577833200004), texture = null))
        val result = cut.findDistinctByTextureAndMapIsNotNullAndTextureIsNotNull()
        assertThat(result).containsExactlyInAnyOrder("Data\\Level\\Hell", "Data\\Level\\Tentacles")
    }
}
