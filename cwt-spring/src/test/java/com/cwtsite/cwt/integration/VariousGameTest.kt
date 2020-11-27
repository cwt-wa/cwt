package com.cwtsite.cwt.integration

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.game.view.CommentCreationDto
import com.cwtsite.cwt.domain.game.view.CommentDto
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.AuthService
import com.cwtsite.cwt.security.SecurityContextHolderFacade
import com.cwtsite.cwt.test.MockitoUtils
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EmbeddedPostgres
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class VariousGameTest {

    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var userRepository: UserRepository
    @Autowired private lateinit var gameRepository: GameRepository
    @Autowired private lateinit var tournamentRepository: TournamentRepository
    @Autowired private lateinit var objectMapper: ObjectMapper

    @MockBean private lateinit var securityContextHolderFacade: SecurityContextHolderFacade
    @MockBean private lateinit var authService: AuthService

    companion object {

        @JvmStatic private var tournament: Tournament? = null
        @JvmStatic private var game: Game? = null
        @JvmStatic private var homeUser: User? = null
        @JvmStatic private var awayUser: User? = null
        @JvmStatic private var author: User? = null
    }


    @BeforeEach
    fun before() {
        if (game == null) {
            homeUser = userRepository.save(User(username = "Zemke", email = "zemke@example.com"))
            awayUser = userRepository.save(User(username = "Rafka", email = "rafka@example.com"))
            author = userRepository.save(User(username = "Mablak", email = "mablak@example.com"))
            tournament = tournamentRepository.save(Tournament())
            game = gameRepository.save(Game(homeUser = homeUser, awayUser = awayUser, tournament = tournament!!))
        }
    }

    @Test
    @WithMockUser
    fun `comment game`() {
        `when`(authService.authUser(MockitoUtils.anyObject())).thenReturn(author)
        `when`(securityContextHolderFacade.authenticationName).thenReturn(author!!.username)
        val comment = CommentCreationDto(body = "Wow, such a comment.", user = author!!.id!!)
        val response = mockMvc
                .perform(post("/api/game/${game!!.id}/comment")
                        .content(objectMapper.writeValueAsString(comment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk)
                .andReturn()
                .response
        val result = objectMapper.readValue(response.contentAsString, CommentDto::class.java)
        assertThat(result!!.body).isEqualTo(comment.body)
        assertThat(result.user.id).isEqualTo(comment.user)
        assertThat(result.user.username).isEqualTo(author!!.username)
    }
}
