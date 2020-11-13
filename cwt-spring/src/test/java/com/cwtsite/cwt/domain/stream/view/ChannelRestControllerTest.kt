package com.cwtsite.cwt.domain.stream.view

import com.cwtsite.cwt.domain.stream.service.StreamService
import com.cwtsite.cwt.test.EntityDefaults
import com.cwtsite.cwt.test.MockitoUtils.anyObject
import com.cwtsite.cwt.twitch.TwitchService
import com.cwtsite.cwt.twitch.model.TwitchVideoDto
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.Test
import kotlin.time.ExperimentalTime

@RunWith(MockitoJUnitRunner::class)
class ChannelRestControllerTest {

    @InjectMocks private lateinit var cut: ChannelRestController
    @Mock private lateinit var twitchService: TwitchService
    @Mock private lateinit var streamService: StreamService

    @ExperimentalTime
    @Test
    fun pollForVideo() {
        val channel = EntityDefaults.channel()
        val twitchVideoDto = mock(TwitchVideoDto::class.java)
        `when`(twitchVideoDto.id).thenReturn("1")
        `when`(twitchVideoDto.userId).thenReturn("2")
        `when`(twitchVideoDto.hasCwtInTitle()).thenReturn(true)
        `when`(twitchService.requestVideos(listOf(channel))).thenReturn(listOf(twitchVideoDto))
        val stream = EntityDefaults.stream(id = twitchVideoDto.id)
        `when`(streamService.saveStreams(listOf(stream))).thenReturn(listOf(stream))
        val game = EntityDefaults.game()
        `when`(streamService.findMatchingGame(stream)).thenReturn(game)
        `when`(streamService.associateGame(stream, game)).thenReturn(stream.copy(game = game))
        runBlocking { cut.pollForVideo(channel, listOf(0, 1)).join() }
        verify(streamService).findMatchingGame(anyObject())
        verify(streamService).associateGame(anyObject(), anyObject())
    }
}
