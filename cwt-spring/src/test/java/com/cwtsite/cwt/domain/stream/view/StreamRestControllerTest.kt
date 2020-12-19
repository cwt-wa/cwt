package com.cwtsite.cwt.domain.stream.view

import com.cwtsite.cwt.domain.game.service.GameService
import com.cwtsite.cwt.domain.stream.service.StreamService
import com.cwtsite.cwt.domain.stream.view.StreamRestController
import com.cwtsite.cwt.domain.stream.view.model.StreamDto
import com.cwtsite.cwt.test.EntityDefaults
import com.cwtsite.cwt.twitch.TwitchService
import com.cwtsite.cwt.twitch.model.TwitchVideoDto

import java.util.Optional

import javax.servlet.http.HttpServletRequest

import kotlin.time.ExperimentalTime

import org.assertj.core.api.Assertions.assertThat

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

import org.springframework.http.HttpStatus


@ExtendWith(MockitoExtension::class)
class StreamRestControllerTest {

    @InjectMocks private lateinit var cut: StreamRestController
    @Mock private lateinit var streamService: StreamService
    @Mock private lateinit var gameService: GameService
    @Mock private lateinit var twitchService: TwitchService

    @Test
    fun `linkGame game and video exist in DB`() {
        val videoId = "1234"
        val stream = EntityDefaults.stream()
        val game = EntityDefaults.game()
        `when`(streamService.findStream(videoId)).thenReturn(Optional.of(stream))
        `when`(gameService.findById(game.id!!)).thenReturn(Optional.of(game))
        `when`(streamService.saveStream(stream)).thenReturn(stream)
        val result = cut.linkGame(videoId, game.id!!)
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).isEqualTo(StreamDto.toDto(stream))
    }

    @Test
    fun `linkGame video does not exist in DB`() {
        val videoId = "1234"
        val game = EntityDefaults.game()
        `when`(gameService.findById(game.id!!)).thenReturn(Optional.of(game))
        `when`(streamService.findStream(videoId)).thenReturn(Optional.empty())
        val twitchVideoDto = createTwitchVideoDto()
        `when`(twitchService.requestVideo(videoId)).thenReturn(twitchVideoDto)
        val channel = EntityDefaults.channel(id = twitchVideoDto.userId)
        `when`(streamService.findChannel(twitchVideoDto.userId)).thenReturn(Optional.of(channel))
        val streamFromTwitchMapped = StreamDto.fromDto(StreamDto.toDto(twitchVideoDto, channel), channel)
        val stream = EntityDefaults.stream()
        `when`(streamService.saveStream(streamFromTwitchMapped)).thenReturn(stream)
        `when`(streamService.saveStream(stream)).thenReturn(stream)
        val result = cut.linkGame(videoId, game.id!!)
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body).isEqualTo(StreamDto.toDto(stream))
    }

    private fun createTwitchVideoDto() =
            TwitchVideoDto(
                    userId = "123F7084",
                    id = "231dn598x",
                    userName = null,
                    title = null,
                    description = null,
                    createdAt = null,
                    publishedAt = null,
                    url = null,
                    thumbnailUrl = null,
                    viewable = null,
                    viewCount = 1,
                    language = null,
                    type = null,
                    duration = null)
}

