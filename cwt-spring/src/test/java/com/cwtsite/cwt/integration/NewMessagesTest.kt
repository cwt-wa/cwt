package com.cwtsite.cwt.integration

import com.cwtsite.cwt.domain.message.entity.enumeration.MessageCategory
import com.cwtsite.cwt.domain.message.view.model.MessageDto
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.AuthService
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.*
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
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*
import kotlin.test.Test


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EmbeddedPostgres
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class NewMessagesTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    @Value("\${jwt.header}")
    private lateinit var tokenHeader: String

    @MockBean
    private lateinit var authService: AuthService

    private val createMessageJson1 = javaClass.getResource("create-message-1.json")!!.readBytes()

    private val createMessageJson2 = javaClass.getResource("create-message-2.json")!!.readBytes()

    private val anyToken = "ANY_TOKEN"

    companion object {

        @JvmStatic
        private var zemkeUser: User? = null

        @JvmStatic
        private var rafkaUser: User? = null

        @JvmStatic
        private var firstMessageCreated: Date? = null
    }

    @Before
    fun setUp() {
        if (zemkeUser == null) zemkeUser = userRepository.save(User(username = "Zemke", email = "Zemke@cwtsite.com"))
        if (rafkaUser == null) rafkaUser = userRepository.save(User(username = "Rafka", email = "Rafka@cwtsite.com"))

        `when`(authService.tokenHeaderName).thenReturn(tokenHeader)
    }

    @Test
    fun `0 add message unauthorized`() {
        mockMvc
                .perform(post("/api/message")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(createMessageJson1))
                .andDo(print())
                .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser
    fun `1 add message`() {
        `when`(authService.getUserFromToken(anyToken)).thenReturn(zemkeUser)

        val response = mockMvc
                .perform(post("/api/message")
                        .header(tokenHeader, anyToken)
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(createMessageJson1))
                .andDo(print())
                .andExpect(status().isOk)
                .andReturn().response.contentAsString

        firstMessageCreated = objectMapper
                .readValue(response, MessageDto::class.java)
                .created
    }

    @Test
    @WithMockUser
    fun `2 add another message`() {
        `when`(authService.getUserFromToken(anyToken)).thenReturn(rafkaUser)

        mockMvc
                .perform(post("/api/message")
                        .header(tokenHeader, anyToken)
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(createMessageJson2))
                .andDo(print())
                .andExpect(status().isOk)
    }

    @Test
    fun `3 read messages`() {
        mockMvc
                .perform(get("/api/message")
                        .contentType(APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.content[1].body", `is`<String>("Hello, this is the body.")))
                .andExpect(jsonPath("$.content[1].author.username", `is`<String>("Zemke")))
                .andExpect(jsonPath("$.content[1].author.id", `is`<Int>(1)))
                .andExpect(jsonPath("$.content[1].newsType", nullValue()))
                .andExpect(jsonPath("$.content[1].category", `is`<String>(MessageCategory.SHOUTBOX.name)))
                .andExpect(jsonPath("$.content[0].body", `is`<String>("Here goes another message")))
                .andExpect(jsonPath("$.content[0].author.username", `is`<String>("Rafka")))
                .andExpect(jsonPath("$.content[0].author.id", `is`<Int>(2)))
                .andExpect(jsonPath("$.content[0].newsType", nullValue()))
                .andExpect(jsonPath("$.content[0].category", `is`<String>(MessageCategory.SHOUTBOX.name)))
                .andExpect(jsonPath("$.content", hasSize<String>(2)))
    }

    @Test
    fun `4 get message after first message`() {
        mockMvc
                .perform(get("/api/message/new")
                        .contentType(APPLICATION_JSON_UTF8)
                        .param("after", firstMessageCreated!!.time.toString()))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$[0].body", `is`<String>("Here goes another message")))
                .andExpect(jsonPath("$[0].author.username", `is`<String>(rafkaUser!!.username)))
                .andExpect(jsonPath("$[0].author.id", `is`<Int>(rafkaUser!!.id!!.toInt())))
                .andExpect(jsonPath("$[0].newsType", nullValue()))
                .andExpect(jsonPath("$[0].category", `is`<String>(MessageCategory.SHOUTBOX.name)))
    }
}
