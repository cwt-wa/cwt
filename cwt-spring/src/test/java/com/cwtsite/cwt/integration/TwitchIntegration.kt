package com.cwtsite.cwt.integration

import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.stream.service.ChannelRepository
import com.cwtsite.cwt.domain.stream.service.StreamRepository
import com.cwtsite.cwt.domain.stream.view.model.StreamDto
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedPostgres
@AutoConfigureMockMvc
@ActiveProfiles(profiles = ["int", "dev"])
class TwitchIntegration {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var channelRepository: ChannelRepository

    @Autowired
    private lateinit var streamRepository: StreamRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `request videos`() {
        val channel = Channel(user = userRepository.save(User(email = "something@something", username = "khamski")), title = "KhamsTV", id = "26027047")
        channelRepository.save(channel)
        val contentAsString = mockMvc
                .perform(get("/api/stream")
                        .param("new", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
        val returnedStreams = listOf(*objectMapper.readValue(contentAsString, Array<StreamDto>::class.java))
                .map { StreamDto.fromDto(it, channel) }
        assertThat(returnedStreams).containsAll(streamRepository.findAll())
        val updatedChannel = channelRepository.findById(channel.id).orElseThrow()
        assertThat(updatedChannel.videoCursor).isNotEmpty()
    }
}
