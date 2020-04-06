package com.cwtsite.cwt.integration

import com.cwtsite.cwt.domain.game.service.GameService
import com.cwtsite.cwt.test.EntityDefaults
import org.hamcrest.Matchers.`is`
import org.junit.FixMethodOrder
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.nio.charset.Charset
import java.util.*
import kotlin.test.Test


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EmbeddedPostgres
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class GameStatsWebIntegration {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var gameService: GameService

    private val statsJson = javaClass.getResource("stats.json")!!.readBytes()

    companion object {

    }

    @Test
    fun `0 retrieve game stats json`() {
        val game = EntityDefaults.game(id = 42)

        `when`(gameService.findById(anyLong()))
                .thenReturn(Optional.of(game))

        `when`(gameService.findGameStats(game))
                .thenReturn("[${statsJson.toString(Charset.defaultCharset())}]")

        mockMvc
                .perform(get("/api/game/42/stats")
                        .contentType(APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$[0].gameId", `is`("10719273")))
    }
}
