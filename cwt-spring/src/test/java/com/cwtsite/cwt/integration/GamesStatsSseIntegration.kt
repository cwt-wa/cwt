package com.cwtsite.cwt.integration

import com.cwtsite.cwt.core.ClockInstance
import com.cwtsite.cwt.core.event.SseEmitterFactory
import com.cwtsite.cwt.core.event.stats.GameStatsEventPublisher
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.GameStats
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.game.service.GameStatsRepository
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository
import com.cwtsite.cwt.test.MockitoUtils.anyObject
import com.cwtsite.cwt.test.MockitoUtils.safeEq
import org.assertj.core.api.Assertions.assertThat
import org.junit.FixMethodOrder
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.sql.Timestamp
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import kotlin.test.Test

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
//@EmbeddedPostgres
//@FlywayTest(locationsForMigrate = ["classpath:db/migration/common", "classpath:db/migration/test"], overrideLocations = true)
@TestPropertySource(properties = [
    "spring.flyway.locations=classpath:db/migration/common,classpath:db/migration/test",
    "spring.jpa.properties.hibernate.default_schema=public",
    "spring.datasource.url=jdbc:postgresql://127.0.0.1:5432/postgres?currentSchema=\"test\"",
    "spring.datasource.username=postgres",
    "spring.datasource.password=postgres",
    "spring.jpa.properties.hibernate.default_schema=\"test\"",
    "spring.flyway.clean-disabled=true",
    "spring.flyway.clean-on-validation-error=false",
    "classpath:db/migration/common,classpath:db/migration/test",
    "stats-sse-timeout=10000"
])
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class GamesStatsSseIntegration {

    @Autowired
    private lateinit var gameRepository: GameRepository

    @Autowired
    private lateinit var gameStatsRepository: GameStatsRepository

    @Autowired
    private lateinit var tournamentRepository: TournamentRepository

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var sseEmitterFactory: SseEmitterFactory

    @Autowired
    private lateinit var eventPublisher: GameStatsEventPublisher

    @MockBean
    private lateinit var clockInstance: ClockInstance

    @Test
    fun `data is sent upon the according application event`() {
        val sseEmitterMock = mock(SseEmitter::class.java)
        `when`(sseEmitterFactory.createInstance())
                .thenReturn(sseEmitterMock)

        val reportedAt = Instant.ofEpochMilli(1587292529755) // Apr 19 2020 12:35 CEST

        `when`(clockInstance.now)
                .thenReturn(Instant.now(Clock.fixed(reportedAt.plusMillis(500), ZoneId.systemDefault())))

        val thirtyDaysAgo: Long = 60 * 60 * 24 * 30
        val game = gameRepository.save(Game(
                tournament = tournamentRepository.save(
                        Tournament(created = Timestamp.from(reportedAt.minusSeconds(thirtyDaysAgo)))),
                reportedAt = Timestamp.from(reportedAt)))

        val preExistingGameStatsData = "{\"pre\":\"existing\"}"
        gameStatsRepository.save(GameStats(
                game = game,
                data = preExistingGameStatsData,
                startedAt = Timestamp.from(Instant.ofEpochMilli(1587292356955))))

        val expectedGameStatsData = "{\"hello\": \"world\", \"time\": ${Instant.now().epochSecond}}"
        `when`(sseEmitterMock.send(anyObject(), anyObject()))
                .thenAnswer { invocation ->
                    assertThat(invocation.getArgument<String>(0)).isEqualTo(preExistingGameStatsData)
                    assertThat(invocation.getArgument<MediaType>(1)).isEqualTo(MediaType.APPLICATION_STREAM_JSON)
                }
                .thenAnswer { invocation ->
                    assertThat(invocation.getArgument<String>(0)).isEqualTo(expectedGameStatsData)
                    assertThat(invocation.getArgument<MediaType>(1)).isEqualTo(MediaType.APPLICATION_STREAM_JSON)
                }

        mockMvc
                .perform(get("/api/game/${game.id}/stats-listen")
                        .contentType(MediaType.APPLICATION_STREAM_JSON))
                .andExpect(status().isOk)
                .andReturn()

        eventPublisher.publish(
                GameStats(
                        data = expectedGameStatsData,
                        game = game))

        verify(sseEmitterMock, times(2)).send(anyObject(), anyObject())
    }

    @Test
    fun `existing data is sent when we're past the timeout and stream is then immediately closed`() {

    }

    @Test
    fun `subscriptions are closed once the emitter completes`() {

    }
}
