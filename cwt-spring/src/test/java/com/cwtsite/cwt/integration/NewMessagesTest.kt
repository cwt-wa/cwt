package com.cwtsite.cwt.integration

import com.cwtsite.cwt.domain.message.entity.enumeration.MessageCategory
import com.cwtsite.cwt.domain.message.view.model.MessageDto
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.AuthService
import com.cwtsite.cwt.test.MockitoUtils
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EmbeddedPostgres
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class NewMessagesTest {

    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var objectMapper: ObjectMapper
    @Autowired private lateinit var userRepository: UserRepository

    @MockBean private lateinit var authService: AuthService

    private val createMessageJson1 = javaClass.getResource("create-message-1.json")!!.readBytes()
    private val createMessageJson2 = javaClass.getResource("create-message-2.json")!!.readBytes()

    companion object {

        @JvmStatic private var zemkeUser: User? = null
        @JvmStatic private var rafkaUser: User? = null
        @JvmStatic private var firstMessageCreated: Instant? = null
    }

    @BeforeEach
    fun setUp() {
        if (zemkeUser == null) zemkeUser = userRepository.save(User(username = "Zemke", email = "Zemke@cwtsite.com"))
        if (rafkaUser == null) rafkaUser = userRepository.save(User(username = "Rafka", email = "Rafka@cwtsite.com"))
        `when`(authService.authUser(MockitoUtils.anyObject())).thenReturn(zemkeUser)
    }

    @Test
    @Order(1)
    fun `add message unauthorized`() {
        mockMvc
                .perform(post("/api/message")
                        .contentType(APPLICATION_JSON)
                        .content(createMessageJson1))
                .andDo(print())
                .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser
    @Order(2)
    fun `add message`() {
        val response = mockMvc
                .perform(post("/api/message")
                        .contentType(APPLICATION_JSON)
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
    @Order(3)
    fun `add another message`() {
        `when`(authService.authUser(MockitoUtils.anyObject())).thenReturn(rafkaUser)
        mockMvc
                .perform(post("/api/message")
                        .contentType(APPLICATION_JSON)
                        .content(createMessageJson2))
                .andDo(print())
                .andExpect(status().isOk)
    }

    @Test
    @Order(4)
    fun `read messages`() {
        mockMvc
                .perform(get("/api/message")
                        .contentType(APPLICATION_JSON)
                        .queryParam("after", firstMessageCreated!!.toEpochMilli().dec().toString()))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$[1].body", `is`<String>("Hello, this is the body.")))
                .andExpect(jsonPath("$[1].author.username", `is`<String>("Zemke")))
                .andExpect(jsonPath("$[1].author.id", `is`<Int>(1)))
                .andExpect(jsonPath("$[1].newsType", nullValue()))
                .andExpect(jsonPath("$[1].category", `is`<String>(MessageCategory.SHOUTBOX.name)))
                .andExpect(jsonPath("$[0].body", `is`<String>("Here goes another message")))
                .andExpect(jsonPath("$[0].author.username", `is`<String>("Rafka")))
                .andExpect(jsonPath("$[0].author.id", `is`<Int>(2)))
                .andExpect(jsonPath("$[0].newsType", nullValue()))
                .andExpect(jsonPath("$[0].category", `is`<String>(MessageCategory.SHOUTBOX.name)))
                .andExpect(jsonPath("$", hasSize<String>(2)))
    }

    @Test
    @Order(5)
    fun `get message after first message`() {
        mockMvc
                .perform(get("/api/message")
                        .contentType(APPLICATION_JSON)
                        .queryParam("after", firstMessageCreated!!.toEpochMilli().toString()))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<String>(1)))
                .andExpect(jsonPath("$[0].body", `is`<String>("Here goes another message")))
                .andExpect(jsonPath("$[0].author.username", `is`<String>("Rafka")))
                .andExpect(jsonPath("$[0].author.id", `is`<Int>(2)))
                .andExpect(jsonPath("$[0].newsType", nullValue()))
                .andExpect(jsonPath("$[0].category", `is`<String>(MessageCategory.SHOUTBOX.name)))
    }
}
