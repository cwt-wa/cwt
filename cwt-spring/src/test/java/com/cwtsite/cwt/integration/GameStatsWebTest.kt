package com.cwtsite.cwt.integration

import com.cwtsite.cwt.core.BinaryOutboundService
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.AuthorityRole
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.AuthService
import com.cwtsite.cwt.test.MockitoUtils.anyObject
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.entity.InputStreamEntity
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.io.File
import java.io.FileInputStream
import java.time.Instant


private const val game1Id = 1559
private const val game2Id = 1513

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EmbeddedPostgres
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class GameStatsWebIntegration {

    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var userRepository: UserRepository
    @Autowired private lateinit var tournamentRepository: TournamentRepository
    @Autowired private lateinit var gameRepository: GameRepository

    @MockBean private lateinit var authService: AuthService
    @MockBean private lateinit var binaryOutboundService: BinaryOutboundService

    private val game1ZipArchive = File(javaClass.getResource("$game1Id.zip").toURI())
    private val game2ZipArchive = File(javaClass.getResource("$game2Id.zip").toURI())
    private val game1StatsJson = listOf(
            javaClass.getResourceAsStream("$game1Id/1.json")!!,
            javaClass.getResourceAsStream("$game1Id/2.json")!!,
            javaClass.getResourceAsStream("$game1Id/3.json")!!,
            javaClass.getResourceAsStream("$game1Id/4.json")!!)
    private val game2StatsJson = listOf(
            javaClass.getResourceAsStream("$game2Id/1.json")!!,
            javaClass.getResourceAsStream("$game2Id/2.json")!!,
            javaClass.getResourceAsStream("$game2Id/3.json")!!,
            javaClass.getResourceAsStream("$game2Id/4.json")!!)

    companion object {
        @JvmStatic private var zemkeUser: User? = null
        @JvmStatic private var rafkaUser: User? = null
        @JvmStatic private var game1: Game? = null
        @JvmStatic private var game2: Game? = null
    }

    @BeforeEach
    fun setUp() {
        if (game1 == null) {
            game1 = gameRepository.save(Game(
                    tournament = tournamentRepository.save(
                            Tournament(created = Instant.now()))))
        }
        if (game2 == null) {
            game2 = gameRepository.save(Game(
                    tournament = tournamentRepository.save(
                            Tournament(created = Instant.now()))))
        }
        if (zemkeUser == null) zemkeUser = userRepository.save(User(username = "nOox", email = "nOox@cwtsite.com"))
        if (rafkaUser == null) rafkaUser = userRepository.save(User(username = "Boolc", email = "Boolc@cwtsite.com"))

        `when`(binaryOutboundService.binaryDataStoreConfigured()).thenReturn(true)
        `when`(binaryOutboundService.waaasConfigured()).thenReturn(true)
    }

    @WithMockUser
    @Test
    @Order(1)
    fun `save replay file`() {
        `when`(authService.authUser(anyObject())).thenReturn(zemkeUser)

        val resMock = createMockHttpResponse(
                InputStreamEntity(game1StatsJson[0]), InputStreamEntity(game1StatsJson[1]),
                InputStreamEntity(game1StatsJson[2]), InputStreamEntity(game1StatsJson[3]))

        `when`(binaryOutboundService.extractGameStats(anyLong(), anyObject()))
                .thenReturn(resMock)

        mockMvc
                .perform(multipart("/api/binary/game/${game1!!.id}/replay")
                        .file(MockMultipartFile("replay", game1ZipArchive.name, "application/zip", FileInputStream(game1ZipArchive)))
                        .param("score-home", "3")
                        .param("score-away", "1")
                        .param("home-user", zemkeUser!!.id.toString())
                        .param("away-user", rafkaUser!!.id.toString())
                )
                .andDo(print())
                .andExpect(status().isCreated)
    }

    @Test
    @Order(2)
    fun `retrieve game stats json`() {
        mockMvc
                .perform(get("/api/game/${game1!!.id}/stats")
                        .contentType(APPLICATION_JSON))
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

    @Test
    @WithMockUser(authorities = [AuthorityRole.ROLE_ADMIN])
    @Order(3)
    fun `save game stats explicitly`() {
        `when`(authService.authUser(anyObject())).thenReturn(zemkeUser)

        val resMock = createMockHttpResponse(
                InputStreamEntity(game2StatsJson[0]), InputStreamEntity(game2StatsJson[1]),
                InputStreamEntity(game2StatsJson[2]), InputStreamEntity(game2StatsJson[3]))

        `when`(binaryOutboundService.extractGameStats(anyLong(), anyObject()))
                .thenAnswer { invocation ->
                    assertThat(invocation.getArgument<File>(1).exists()).isTrue()
                    resMock
                }

        mockMvc
                .perform(multipart("/api/binary/game/${game2!!.id}/stats")
                        .file(MockMultipartFile("replay", game2ZipArchive.name, "application/zip", FileInputStream(game2ZipArchive)))
                        .param("home-user", zemkeUser!!.id.toString())
                        .param("away-user", rafkaUser!!.id.toString())
                )
                .andDo(print())
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$", hasSize<Any>(4)))
                .andExpect(jsonPath("$[0].gameId", `is`("10504579")))
                .andExpect(jsonPath("$[1].gameId", `is`("10504629")))
                .andExpect(jsonPath("$[2].gameId", `is`("10505983")))
                .andExpect(jsonPath("$[3].gameId", `is`("10505983")))
                .andExpect(jsonPath("$[0].startedAt", `is`("2019-09-16 16:58:33 GMT")))
                .andExpect(jsonPath("$[1].startedAt", `is`("2019-09-16 17:31:45 GMT")))
                .andExpect(jsonPath("$[2].startedAt", `is`("2019-09-17 16:44:46 GMT")))
                .andExpect(jsonPath("$[3].startedAt", `is`("2019-09-17 17:02:45 GMT")))
                .andDo(print())
    }

    private fun createMockHttpResponse(vararg mocks: InputStreamEntity): CloseableHttpResponse =
            `when`(mock(CloseableHttpResponse::class.java).entity)
                    .thenReturn(mocks[0], *mocks.toList().subList(1, mocks.size).toTypedArray())
                    .getMock()
}
