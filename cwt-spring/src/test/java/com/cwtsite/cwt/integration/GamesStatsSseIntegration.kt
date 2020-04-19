package com.cwtsite.cwt.integration

import com.cwtsite.cwt.core.ClockInstance
import com.cwtsite.cwt.core.event.SseEmitterFactory
import com.cwtsite.cwt.core.event.stats.GameStatsEventListener
import com.cwtsite.cwt.core.event.stats.GameStatsEventPublisher
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.GameStats
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.game.service.GameStatsRepository
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository
import com.cwtsite.cwt.test.MockitoUtils.anyObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
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
@EmbeddedPostgres
@TestPropertySource(properties = ["stats-sse-timeout=10000"])
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

    @SpyBean
    private lateinit var eventListener: GameStatsEventListener

    @MockBean
    private lateinit var clockInstance: ClockInstance

    private val sseEmitterMock = mock(SseEmitter::class.java)

    private val reportedAt = Instant.ofEpochMilli(1587292529755) // Apr 19 2020 12:35 CEST

    private lateinit var game: Game

    @Before
    fun setUp() {
        `when`(sseEmitterFactory.createInstance())
                .thenReturn(sseEmitterMock)
        val thirtyDaysAgo: Long = 60 * 60 * 24 * 30
        game = gameRepository.save(Game(
                tournament = tournamentRepository.save(
                        Tournament(created = Timestamp.from(reportedAt.minusSeconds(thirtyDaysAgo)))),
                reportedAt = Timestamp.from(reportedAt)))
    }

    @Test
    fun `data is sent upon the according application event`() {
        `when`(clockInstance.now)
                .thenReturn(Instant.now(Clock.fixed(reportedAt.plusMillis(500), ZoneId.systemDefault())))

        val preExistingGameStatsData = "{\"pre\":\"existing\"}"
        gameStatsRepository.save(GameStats(
                game = game,
                data = preExistingGameStatsData,
                startedAt = Timestamp.from(Instant.ofEpochMilli(1587292356955))))

        val expectedGameStatsData = "{\"hello\": \"world\", \"time\": ${Instant.now().epochSecond}}"
        `when`(sseEmitterMock.send(anyObject(), anyObject()))
                .thenAnswer { invocation ->
                    assertThat(invocation.getArgument<String>(0)).isEqualTo(preExistingGameStatsData)
                    assertThat(invocation.getArgument<MediaType>(1)).isEqualTo(MediaType.TEXT_EVENT_STREAM)
                }
                .thenAnswer { invocation ->
                    assertThat(invocation.getArgument<String>(0)).isEqualTo(expectedGameStatsData)
                    assertThat(invocation.getArgument<MediaType>(1)).isEqualTo(MediaType.TEXT_EVENT_STREAM)
                }

        performMockMvc()

        eventPublisher.publish(
                GameStats(
                        data = expectedGameStatsData,
                        game = game))

        verify(sseEmitterMock, times(2)).send(anyObject(), anyObject())
    }

    @Test
    fun `existing data is sent when we're past the timeout and stream is then immediately closed`() {
        `when`(clockInstance.now)
                .thenReturn(Instant.now(Clock.fixed(reportedAt.plusMillis(90000), ZoneId.systemDefault())))

        val preExistingGameStatsData = "{\"pre\":\"existing\"}"
        gameStatsRepository.save(GameStats(
                game = game,
                data = preExistingGameStatsData,
                startedAt = Timestamp.from(Instant.ofEpochMilli(1587292356955))))

        `when`(sseEmitterMock.send(anyObject(), anyObject()))
                .thenAnswer { invocation ->
                    assertThat(invocation.getArgument<String>(0)).isEqualTo(preExistingGameStatsData)
                    assertThat(invocation.getArgument<MediaType>(1)).isEqualTo(MediaType.TEXT_EVENT_STREAM)
                }

        performMockMvc()

        verify(sseEmitterMock).send(anyObject(), anyObject())
        verify(sseEmitterMock).complete()
        verifyZeroInteractions(eventListener)
    }

    private fun performMockMvc() {
        mockMvc
                .perform(get("/api/game/${game.id}/stats-listen")
                        .contentType(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isOk)
                .andReturn()
    }
}
