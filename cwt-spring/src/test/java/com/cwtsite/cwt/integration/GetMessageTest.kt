package com.cwtsite.cwt.integration

import com.cwtsite.cwt.domain.message.entity.Message
import com.cwtsite.cwt.domain.message.entity.enumeration.MessageCategory
import com.cwtsite.cwt.domain.message.service.MessageRepository
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.AuthService
import com.cwtsite.cwt.test.MockitoUtils
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EmbeddedPostgres
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class GetMessageTest {

    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var userRepository: UserRepository
    @Autowired private lateinit var messageRepository: MessageRepository

    @MockBean private lateinit var authService: AuthService

    companion object {

        @JvmStatic private var zemkeUser: User? = null
        @JvmStatic private var rafkaUser: User? = null
        @JvmStatic private var uninvolvedUser: User? = null
        @JvmStatic private var messages: MutableList<Message> = mutableListOf()
    }

    @BeforeEach
    fun before() {
        if (zemkeUser == null) {
            zemkeUser = userRepository.save(User(username = "Zemke", email = "zemke@cwtsite.com"))
        }
        if (rafkaUser == null) {
            rafkaUser = userRepository.save(User(username = "Rafka", email = "rafka@cwtsite.com"))
        }
        if (uninvolvedUser == null) {
            uninvolvedUser = userRepository.save(User(username = "Uninvolved", email = "uninvolved@cwtsite.com"))
        }
        if (messages.isEmpty()) {
            messages.add(messageRepository.save(Message(body = "First shoutbox", author = zemkeUser!!, category = MessageCategory.SHOUTBOX)))
            messages.add(messageRepository.save(Message(body = "Second shoutbox", author = rafkaUser!!, category = MessageCategory.SHOUTBOX)))
            messages.add(messageRepository.save(Message(body = "@Rafka First private", author = zemkeUser!!, category = MessageCategory.PRIVATE, recipients = mutableListOf(rafkaUser!!))))
            messages.add(messageRepository.save(Message(body = "First News", author = zemkeUser!!, category = MessageCategory.SHOUTBOX)))
            messages.add(messageRepository.save(Message(body = "@Zemke Second private", author = zemkeUser!!, category = MessageCategory.PRIVATE, recipients = mutableListOf(zemkeUser!!))))
            messages.add(messageRepository.save(Message(body = "@Uninvolved Third private", author = rafkaUser!!, category = MessageCategory.PRIVATE, recipients = mutableListOf(uninvolvedUser!!))))
        }

        `when`(authService.authUser(MockitoUtils.anyObject())).thenReturn(zemkeUser)
    }

    @Test
    @WithMockUser
    fun `get message after without uninvolved private messages`() {
        val expected = listOf(messages[4], messages[3], messages[2], messages[1]).sortedByDescending { it.created }
        mockMvc
            .perform(
                get("/api/message")
                    .queryParam("after", messages[0].created!!.toEpochMilli().toString())
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<String>(4)))
            .andExpect(jsonPath("$[0].id", `is`(expected[0].id!!.toInt())))
            .andExpect(jsonPath("$[1].id", `is`(expected[1].id!!.toInt())))
            .andExpect(jsonPath("$[2].id", `is`(expected[2].id!!.toInt())))
            .andExpect(jsonPath("$[3].id", `is`(expected[3].id!!.toInt())))
    }

    @Test
    @WithMockUser
    fun `get message before without uninvolved private messages`() {
        val expected = listOf(messages[0], messages[1]).sortedByDescending { it.created }
        mockMvc
            .perform(
                get("/api/message")
                    .queryParam("before", messages[2].created!!.toEpochMilli().toString())
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<String>(2)))
            .andExpect(jsonPath("$[0].id", `is`(expected[0].id!!.toInt())))
            .andExpect(jsonPath("$[1].id", `is`(expected[1].id!!.toInt())))
    }

    @Test
    @WithMockUser
    fun `only private messages`() {
        val expected = listOf(messages[2], messages[4]).sortedByDescending { it.created }
        mockMvc
            .perform(
                get("/api/message")
                    .queryParam("after", messages[1].created!!.toEpochMilli().toString())
                    .queryParam("category", MessageCategory.PRIVATE.name)
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<String>(2)))
            .andExpect(jsonPath("$[0].id", `is`(expected[0].id!!.toInt())))
            .andExpect(jsonPath("$[1].id", `is`(expected[1].id!!.toInt())))
    }

    @Test
    @WithMockUser
    fun `bad request when before xor after query param violated`() {
        mockMvc
            .perform(
                get("/api/message")
                    .queryParam("before", messages[2].created!!.toEpochMilli().toString())
                    .queryParam("after", messages[2].created!!.toEpochMilli().toString())
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest)
        mockMvc
            .perform(
                get("/api/message")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isBadRequest)
    }

    @Test
    @WithMockUser
    fun `limit result set`() {
        `when`(authService.authUser(MockitoUtils.anyObject())).thenReturn(null)
        val expected = messages
            .filter { it.category != MessageCategory.PRIVATE }
            .sortedByDescending { it.created }
        mockMvc
            .perform(
                get("/api/message")
                    .queryParam("after", "0")
                    .queryParam("size", "2")
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<String>(2)))
            .andExpect(jsonPath("$[0].id", `is`(expected[0].id!!.toInt())))
            .andExpect(jsonPath("$[1].id", `is`(expected[1].id!!.toInt())))
    }
}
