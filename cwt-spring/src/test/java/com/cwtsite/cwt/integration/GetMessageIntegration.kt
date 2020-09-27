package com.cwtsite.cwt.integration

import com.cwtsite.cwt.domain.message.entity.Message
import com.cwtsite.cwt.domain.message.entity.enumeration.MessageCategory
import com.cwtsite.cwt.domain.message.service.MessageRepository
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.AuthService
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EmbeddedPostgres
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class GetMessageIntegration {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var messageRepository: MessageRepository

    @Value("\${jwt.header}")
    private lateinit var tokenHeader: String

    @MockBean
    private lateinit var authService: AuthService

    private val anyToken = "ANY_TOKEN"

    companion object {

        @JvmStatic
        private var zemkeUser: User? = null

        @JvmStatic
        private var rafkaUser: User? = null

        @JvmStatic
        private var uninvolvedUser: User? = null

        @JvmStatic
        private var messages: MutableList<Message> = mutableListOf()
    }

    @Before
    fun setUp() {
        if (zemkeUser == null) zemkeUser = userRepository.save(User(username = "Zemke", email = "Zemke@cwtsite.com"))
        if (rafkaUser == null) rafkaUser = userRepository.save(User(username = "Rafka", email = "Rafka@cwtsite.com"))
        if (uninvolvedUser == null) uninvolvedUser = userRepository.save(User(username = "Uninvolved", email = "uninvolved@cwtsite.com"))
        `when`(authService.tokenHeaderName).thenReturn(tokenHeader)
        `when`(authService.getUserFromToken(anyToken)).thenReturn(zemkeUser)
        if (messages.isEmpty()) {
            messages.add(messageRepository.save(Message(body = "First shoutbox", author = zemkeUser!!, category = MessageCategory.SHOUTBOX)))
            messages.add(messageRepository.save(Message(body = "Second shoutbox", author = rafkaUser!!, category = MessageCategory.SHOUTBOX)))
            messages.add(messageRepository.save(Message(body = "@Rafka First private", author = zemkeUser!!, category = MessageCategory.PRIVATE, recipients = mutableListOf(rafkaUser!!))))
            messages.add(messageRepository.save(Message(body = "First News", author = zemkeUser!!, category = MessageCategory.SHOUTBOX)))
            messages.add(messageRepository.save(Message(body = "@Zemke Second private", author = zemkeUser!!, category = MessageCategory.PRIVATE, recipients = mutableListOf(zemkeUser!!))))
            messages.add(messageRepository.save(Message(body = "@Uninvolved Third private", author = rafkaUser!!, category = MessageCategory.PRIVATE, recipients = mutableListOf(uninvolvedUser!!))))
        }
    }

    @Test
    fun `get message after without uninvolved private messages`() {
        mockMvc
                .perform(get("/api/message")
                        .queryParam("after", messages[0].created!!.time.toString())
                        .header(tokenHeader, anyToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<String>(4)))
                .andExpect(jsonPath("$[0].id", `is`(messages[1].id!!.toInt())))
                .andExpect(jsonPath("$[1].id", `is`(messages[2].id!!.toInt())))
                .andExpect(jsonPath("$[2].id", `is`(messages[3].id!!.toInt())))
                .andExpect(jsonPath("$[3].id", `is`(messages[4].id!!.toInt())))
    }

    @Test
    fun `get message before without uninvolved private messages`() {
        mockMvc
                .perform(get("/api/message")
                        .queryParam("before", messages[2].created!!.time.toString())
                        .header(tokenHeader, anyToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<String>(2)))
                .andExpect(jsonPath("$[0].id", `is`(messages[0].id!!.toInt())))
                .andExpect(jsonPath("$[1].id", `is`(messages[1].id!!.toInt())))
    }

    @Test
    fun `only private messages`() {
        mockMvc
                .perform(get("/api/message")
                        .queryParam("after", messages[1].created!!.time.toString())
                        .queryParam("category", MessageCategory.PRIVATE.name)
                        .header(tokenHeader, anyToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<String>(2)))
                .andExpect(jsonPath("$[0].id", `is`(messages[2].id!!.toInt())))
                .andExpect(jsonPath("$[1].id", `is`(messages[4].id!!.toInt())))
    }

    @Test
    fun `bad request when before xor after query param given`() {
        mockMvc
                .perform(get("/api/message")
                        .queryParam("before", messages[2].created!!.time.toString())
                        .queryParam("after", messages[2].created!!.time.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest)
        mockMvc
                .perform(get("/api/message")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest)
    }

    @Test
    fun `limit result set`() {
        mockMvc
                .perform(get("/api/message")
                        .queryParam("after", "0")
                        .queryParam("size", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<String>(2)))
                .andExpect(jsonPath("$[0].id", `is`(messages[0].id!!.toInt())))
                .andExpect(jsonPath("$[1].id", `is`(messages[1].id!!.toInt())))
    }
}
