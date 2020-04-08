package com.cwtsite.cwt.integration

import com.cwtsite.cwt.core.BinaryOutboundService
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.game.service.GameService
import com.cwtsite.cwt.domain.game.service.GameStatsRepository
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.AuthService
import com.cwtsite.cwt.test.MockitoUtils.anyObject
import org.apache.http.HttpEntity
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.sql.Timestamp
import java.time.Instant
import kotlin.test.Test


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EmbeddedPostgres
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class GameStatsWebIntegration {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var gameService: GameService

    @Autowired
    private lateinit var tournamentRepository: TournamentRepository

    @Autowired
    private lateinit var gameRepository: GameRepository

    @Autowired
    private lateinit var gameStatsRepository: GameStatsRepository

    @Value("\${jwt.header}")
    private lateinit var tokenHeader: String

    @MockBean
    private lateinit var authService: AuthService

    @MockBean
    private lateinit var binaryOutboundService: BinaryOutboundService

    private val anyToken = "ANY_TOKEN"

    private val rarFile = File(javaClass.getResource("1011.rar").toURI())
    private val statsJson1 = javaClass.getResourceAsStream("1.json")!!
    private val statsJson2 = javaClass.getResourceAsStream("2.json")!!
    private val statsJson3 = javaClass.getResourceAsStream("3.json")!!
    private val statsJson4 = javaClass.getResourceAsStream("4.json")!!

    companion object {

        @JvmStatic
        private var zemkeUser: User? = null

        @JvmStatic
        private var rafkaUser: User? = null

        @JvmStatic
        private var game: Game? = null
    }

    @Before
    fun setUp() {
        game = gameRepository.save(Game(
                tournament = tournamentRepository.save(
                        Tournament(created = Timestamp.from(Instant.now())))))

        zemkeUser = userRepository.save(User(username = "Zemke", email = "Zemke@cwtsite.com"))
        rafkaUser = userRepository.save(User(username = "Rafka", email = "Rafka@cwtsite.com"))
    }

    @Test
    @WithMockUser
    @Transactional
    fun `1 save game stats json`() {

        `when`(authService.tokenHeaderName).thenReturn(tokenHeader)
        `when`(authService.getUserFromToken(anyToken)).thenReturn(zemkeUser)

        val statsJsonMock1 = createStatsResponse(statsJson1)
        val statsJsonMock2 = createStatsResponse(statsJson2)
        val statsJsonMock3 = createStatsResponse(statsJson3)
        val statsJsonMock4 = createStatsResponse(statsJson4)

        `when`(binaryOutboundService.sendMultipartEntity(anyString(), anyObject(), anyString(), anyString(), anyString()))
                .thenReturn(statsJsonMock1)
                .thenReturn(statsJsonMock2)
                .thenReturn(statsJsonMock3)
                .thenReturn(statsJsonMock4)

        val inputStream = FileInputStream(rarFile)
        mockMvc
                .perform(multipart("/api/binary/game/${game!!.id}/replay")
                        .file(MockMultipartFile("replay", rarFile.name, "application/x-rar-compressed", inputStream))
                        .header(tokenHeader, anyToken)
                        .param("score-home", "3")
                        .param("score-away", "0")
                        .param("home-user", zemkeUser!!.id.toString())
                        .param("away-user", rafkaUser!!.id.toString())
                )
                .andDo(print())
                .andExpect(status().isCreated)
    }

    @Test
    fun `2 retrieve game stats json`() {
        val response = mockMvc
                .perform(get("/api/game/${game!!.id}/stats")
                        .contentType(APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<Any>(4)))
                .andExpect(jsonPath("$[0].gameId", `is`("10719273")))
        // todo more assertions
    }

    private fun createStatsResponse(statsJson: InputStream): HttpEntity {
        val httpEntityMock = mock(HttpEntity::class.java)
        `when`(httpEntityMock.content).thenReturn(statsJson)
        return httpEntityMock
    }
}
