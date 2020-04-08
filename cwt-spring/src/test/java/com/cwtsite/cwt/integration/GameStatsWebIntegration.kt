package com.cwtsite.cwt.integration

import com.cwtsite.cwt.controller.waGameMimeType
import com.cwtsite.cwt.core.BinaryOutboundService
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.AuthService
import com.cwtsite.cwt.test.MockitoUtils.anyObject
import com.cwtsite.cwt.test.MockitoUtils.safeEq
import org.apache.http.entity.InputStreamEntity
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
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
import java.io.File
import java.io.FileInputStream
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
    private lateinit var tournamentRepository: TournamentRepository

    @Autowired
    private lateinit var gameRepository: GameRepository

    @Value("\${jwt.header}")
    private lateinit var tokenHeader: String

    @Value("\${waaas-endpoint}")
    private lateinit var waaasEndpoint: String

    @MockBean
    private lateinit var authService: AuthService

    @MockBean
    private lateinit var binaryOutboundService: BinaryOutboundService

    private val anyToken = "ANY_TOKEN"

    private val zipArchive = File(javaClass.getResource("1559.zip").toURI())
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
        if (game == null) {
            game = gameRepository.save(Game(
                    tournament = tournamentRepository.save(
                            Tournament(created = Timestamp.from(Instant.now())))))
        }
        if (zemkeUser == null) zemkeUser = userRepository.save(User(username = "nOox", email = "nOox@cwtsite.com"))
        if (rafkaUser == null) rafkaUser = userRepository.save(User(username = "Boolc", email = "Boolc@cwtsite.com"))
        `when`(authService.tokenHeaderName).thenReturn(tokenHeader)
        `when`(authService.getUserFromToken(anyToken)).thenReturn(zemkeUser)
    }

    @Test
    @WithMockUser
    fun `1 save game stats json`() {
        `when`(binaryOutboundService.sendMultipartEntity(
                safeEq(waaasEndpoint), anyObject(), safeEq(waGameMimeType), anyString(), anyString()))
                .thenAnswer { InputStreamEntity(statsJson1) }
                .thenAnswer { InputStreamEntity(statsJson2) }
                .thenAnswer { InputStreamEntity(statsJson3) }
                .thenAnswer { InputStreamEntity(statsJson4) }

        val inputStream = FileInputStream(zipArchive)
        mockMvc
                .perform(multipart("/api/binary/game/${game!!.id}/replay")
                        .file(MockMultipartFile("replay", zipArchive.name, "application/zip", inputStream))
                        .header(tokenHeader, anyToken)
                        .param("score-home", "3")
                        .param("score-away", "1")
                        .param("home-user", zemkeUser!!.id.toString())
                        .param("away-user", rafkaUser!!.id.toString())
                )
                .andDo(print())
                .andExpect(status().isCreated)
    }

    @Test
    fun `2 retrieve game stats json`() {
        mockMvc
                .perform(get("/api/game/${game!!.id}/stats")
                        .contentType(APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<Any>(4)))
                .andExpect(jsonPath("$[0].gameId", `is`("10534216")))
                .andExpect(jsonPath("$[1].gameId", `is`("10534216")))
                .andExpect(jsonPath("$[2].gameId", `is`("10534216")))
                .andExpect(jsonPath("$[3].gameId", `is`("10534216")))
                .andExpect(jsonPath("$[0].startedAt", `is`("2019-10-06 12:05:33 GMT")))
                .andExpect(jsonPath("$[1].startedAt", `is`("2019-10-06 12:26:30 GMT")))
                .andExpect(jsonPath("$[2].startedAt", `is`("2019-10-06 12:45:24 GMT")))
                .andExpect(jsonPath("$[3].startedAt", `is`("2019-10-06 13:10:39 GMT")))
    }
}
