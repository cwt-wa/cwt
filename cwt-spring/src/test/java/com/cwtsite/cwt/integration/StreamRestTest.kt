package com.cwtsite.cwt.integration

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.stream.view.model.StreamDto
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository
import com.cwtsite.cwt.domain.stream.service.ChannelRepository
import com.cwtsite.cwt.domain.stream.service.StreamRepository
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.AuthorityRole
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.stream.entity.Channel
import com.fasterxml.jackson.databind.ObjectMapper

import org.assertj.core.api.Assertions.assertThat

import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EmbeddedPostgres
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class StreamRestTest {

    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var objectMapper: ObjectMapper
    @Autowired private lateinit var gameRepository: GameRepository
    @Autowired private lateinit var userRepository: UserRepository
    @Autowired private lateinit var channelRepository: ChannelRepository
    @Autowired private lateinit var tournamentRepository: TournamentRepository
    @Autowired private lateinit var streamRepository: StreamRepository

    companion object {
        @JvmStatic private var game1: Game? = null
        @JvmStatic private var game2: Game? = null
        @JvmStatic private var streamId: String? = null
        @JvmStatic private var user: User? = null
    }

    @Test
    @Order(1)
    fun `setup`() {
        val tournament = tournamentRepository.save(Tournament())
        game1 = gameRepository.save(Game(tournament = tournament))
        game2 = gameRepository.save(Game(tournament = tournament))
        user = userRepository.save(User(
                username = "Zemke", email = "zemke@cwtsite.com", password = "pw"))
    }

    @Test
    @Order(2)
    @WithMockUser(authorities = [AuthorityRole.ROLE_ADMIN])
    fun `link stream not yet in database channel not registered`() {
        mockMvc
                .perform(post("/api/stream/vimwq/game/${game1!!.id}/link")
                            .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.message").value("Channel 26027047 of video is not registered."))
    }

    @Test
    @Order(3)
    @WithMockUser(authorities = [AuthorityRole.ROLE_ADMIN])
    fun `link stream not yet in database`() {
        channelRepository.save(Channel(id = "26027047", user = user!!, title = "EpicTV"))
        val streamId = "1234"
        val response = mockMvc
                .perform(post("/api/stream/${streamId}/game/${game1!!.id}/link")
                            .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
                .let { objectMapper.readValue(it, StreamDto::class.java) }
        StreamRestTest.streamId = response.id
        assertThat(StreamRestTest.streamId).isEqualTo(streamId)
        assertThat(response.game!!.id).isEqualTo(game1!!.id!!)
    }

    @Test
    @Order(4)
    @WithMockUser
    fun `get stream just linked`() {
        val response = mockMvc
                .perform(get("/api/stream/${streamId}"))
                .andDo(print())
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
                .let { objectMapper.readValue(it, StreamDto::class.java) }
        assertThat(response.id).isEqualTo(StreamRestTest.streamId)
        assertThat(response.game!!.id).isEqualTo(game1!!.id)
    }

    @Test
    @Order(5)
    @WithMockUser(authorities = [AuthorityRole.ROLE_ADMIN])
    fun `update game link`() {
        val response = mockMvc
                .perform(post("/api/stream/${streamId}/game/${game2!!.id}/link")
                            .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
                .let { objectMapper.readValue(it, StreamDto::class.java) }
        assertThat(response.game!!.id).isEqualTo(game2!!.id!!)
    }

    @Test
    @Order(6)
    @WithMockUser(authorities = [AuthorityRole.ROLE_ADMIN])
    fun `unlink stream`() {
        val response = mockMvc
                .perform(delete("/api/stream/${streamId}/link")
                            .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
                .let { objectMapper.readValue(it, StreamDto::class.java) }
        assertThat(response.id).isEqualTo(streamId)
        assertThat(response.game).isNull()
    }

    @Test
    @Order(7)
    @WithMockUser(authorities = [AuthorityRole.ROLE_ADMIN])
    fun `remove stream`() {
        assertThat(streamRepository.findAll()).isNotEmpty()
        mockMvc
                .perform(delete("/api/stream/${streamId}")
                            .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk)
        assertThat(streamRepository.findAll()).isEmpty()
    }
}

