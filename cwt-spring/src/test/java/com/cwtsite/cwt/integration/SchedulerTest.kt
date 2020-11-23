package com.cwtsite.cwt.integration

import com.cwtsite.cwt.domain.schedule.view.model.ScheduleCreationDto
import com.cwtsite.cwt.domain.schedule.view.model.ScheduleDto
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.AuthService
import com.cwtsite.cwt.domain.user.view.model.JwtAuthenticationRequest
import com.cwtsite.cwt.domain.user.view.model.JwtAuthenticationResponse
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EmbeddedPostgres
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class SchedulerTest {

    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var objectMapper: ObjectMapper
    @Autowired private lateinit var userRepository: UserRepository
    @Autowired private lateinit var authService: AuthService
    @Value("\${jwt.header}") private lateinit var tokenHeader: String

    private val appointment = 1606156145446

    companion object {
        @JvmStatic private var zemkeUser: User? = null
        @JvmStatic private var rafkaUser: User? = null
        @JvmStatic private var zemkeToken: String? = null
        @JvmStatic private val zemkePassword: String = "zemkePassword"
        @JvmStatic private var scheduleId: Long? = null
    }

    @Test
    @Order(1)
    fun `register Rafka and Zemke`() {
        zemkeUser = userRepository.save(User(
                username = "Zemke", email = "zemke@cwtsite.com",
                password = authService.createHash(zemkePassword)))
        rafkaUser = userRepository.save(User(
                username = "Rafka", email = "rafka@cwtsite.com",
                password = authService.createHash("rafkaPassword")))
    }

    @Test
    @Order(2)
    fun `login Zemke`() {
        zemkeToken = login(zemkeUser!!.username, zemkePassword)
    }

    @Test
    @Order(3)
    fun `Zemke schedule a game with Rafka`() {
        val schedule = ScheduleCreationDto(
                author = zemkeUser!!.id!!,
                opponent = rafkaUser!!.id!!,
                appointment = Date(appointment))
       mockMvc
                .perform(post("/api/schedule")
                        .header(tokenHeader, zemkeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(schedule)))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").isNumber)
                .andExpect(jsonPath("$.homeUser.id").value(zemkeUser!!.id!!))
                .andExpect(jsonPath("$.homeUser.username").value(zemkeUser!!.username))
                .andExpect(jsonPath("$.awayUser.id").value(rafkaUser!!.id!!))
                .andExpect(jsonPath("$.awayUser.username").value(rafkaUser!!.username))
                .andExpect(jsonPath("$.appointment").value("2020-11-23T18:29:05.446Z"))
                .andReturn().response.contentAsString.let {
                    scheduleId = objectMapper.readValue(it, ScheduleDto::class.java).id
                }
    }

    @Test
    @Order(4)
    fun `get scheduled games`() {
        val response = mockMvc
                .perform(get("/api/schedule")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk)
                .andReturn()
                .response
        val schedules = objectMapper.readValue(
                response.contentAsString, object : TypeReference<List<ScheduleDto>>() {})
        assertThat(schedules).anySatisfy {
            assertThat(it.id).isEqualTo(scheduleId)
            assertThat(it.homeUser.username).isEqualTo(zemkeUser!!.username)
            assertThat(it.awayUser.username).isEqualTo(rafkaUser!!.username)
            assertThat(it.homeUser.id).isEqualTo(zemkeUser!!.id)
            assertThat(it.awayUser.id).isEqualTo(rafkaUser!!.id)
            assertThat(it.appointment.time).isEqualTo(appointment)
            assertThat(it.author.id).isEqualTo(zemkeUser!!.id)
            assertThat(it.author.username).isEqualTo(zemkeUser!!.username)
            assertThat(it.streams).isEmpty()
        }
    }

    fun login(username: String, password: String): String {
        val authReq = JwtAuthenticationRequest(username, password)
        val resAsString = mockMvc
                .perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authReq)))
                .andExpect(status().isOk)
                .andReturn()
                .response.contentAsString
        val responseBody = objectMapper.readValue(resAsString, JwtAuthenticationResponse::class.java)
        return responseBody!!.token
    }
}
