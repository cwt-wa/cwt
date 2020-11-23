package com.cwtsite.cwt.integration

import com.cwtsite.cwt.core.event.SseEmitterFactory
import com.cwtsite.cwt.core.event.SseEmitterWrapper
import com.cwtsite.cwt.domain.message.entity.Message
import com.cwtsite.cwt.domain.message.entity.enumeration.MessageCategory
import com.cwtsite.cwt.domain.message.service.MessageService
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.UserService
import com.cwtsite.cwt.test.MockitoUtils
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.mockito.Mock
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.sql.Timestamp

@RunWith(SpringRunner::class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["spring.profiles.include=sync"])
@AutoConfigureMockMvc
@EmbeddedPostgres
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MessageEventHandlingTest {

    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var messageService: MessageService
    @Autowired private lateinit var userService: UserService
    @MockBean private lateinit var sseEmitterFactory: SseEmitterFactory
    @Mock private lateinit var sseEmitter: SseEmitterWrapper

    companion object {
        private var user: User? = null
    }

    @Before
    fun setup() {
        `when`(sseEmitterFactory.createInstance()).thenReturn(sseEmitter)
        if (user == null) user = userService.saveUser(User(username = "name", email = "master@example.com"))
    }

    @Test
    @WithMockUser
    fun handle() {
        mockMvc
                .perform(get("/api/message/listen").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk)
        val message = Message(
                body = "Everybody needs to know this!",
                author = user!!,
                category = MessageCategory.SHOUTBOX,
                created = Timestamp(1605483348499)
        )
        messageService.save(message)
        verify(sseEmitter).send("EVENT", message)
    }

    @Test
    @WithMockUser
    fun doNotPublishPrivateMessage() {
        mockMvc
                .perform(get("/api/message/listen").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk)
        val author = userService.saveUser(User(username = "Author", email = "author@example.com"))
        val recipient = userService.saveUser(User(username = "HelloUser", email = "hello@example.com"))
        val message = Message(
                body = "Everybody needs to know this!",
                author = author,
                category = MessageCategory.PRIVATE,
                recipients = mutableListOf(recipient),
                created = Timestamp(1605483348499)
        )
        messageService.save(message)
        verify(sseEmitter, never()).send(MockitoUtils.anyObject(), MockitoUtils.anyObject())
    }
}
