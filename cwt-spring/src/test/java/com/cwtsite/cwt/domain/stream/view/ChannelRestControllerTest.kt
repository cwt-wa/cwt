package com.cwtsite.cwt.domain.stream.view

import com.cwtsite.cwt.domain.stream.service.StreamService
import com.cwtsite.cwt.domain.user.service.AuthService
import com.cwtsite.cwt.test.EntityDefaults
import com.cwtsite.cwt.test.MockitoUtils.anyObject
import com.cwtsite.cwt.twitch.TwitchService
import com.cwtsite.cwt.twitch.model.TwitchVideoDto
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import javax.servlet.http.HttpServletRequest
import kotlin.test.Test
import kotlin.time.ExperimentalTime

@RunWith(MockitoJUnitRunner::class)
class ChannelRestControllerTest {

    @InjectMocks private lateinit var cut: ChannelRestController
    @Mock private lateinit var twitchService: TwitchService
    @Mock private lateinit var streamService: StreamService
    @Mock private lateinit var authService: AuthService

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

    @Test
    fun writeAccess() {
        val user = EntityDefaults.user()
        val channel = EntityDefaults.channel(user = user)
        `when`(authService.authUser(anyObject())).thenReturn(user)
        `when`(streamService.findChannelByUser(user)).thenReturn(channel)
        val actual = cut.writeAccess(channel.title, mock(HttpServletRequest::class.java))
        assertThat(actual.body!!).isTrue()
    }

    @Test
    fun `writeAccess fail by user with another channel`() {
        val authUser = EntityDefaults.user(id = 12341234)
        val channel = EntityDefaults.channel()
        `when`(authService.authUser(anyObject())).thenReturn(authUser)
        `when`(streamService.findChannelByUser(authUser)).thenReturn(channel)
        val actual = cut.writeAccess(channel.title, mock(HttpServletRequest::class.java))
        assertThat(actual.body!!).isFalse()
    }

    @Test
    fun `writeAccess fail by user without a channel at all`() {
        val authUser = EntityDefaults.user(id = 12341234)
        `when`(authService.authUser(anyObject())).thenReturn(authUser)
        `when`(streamService.findChannelByUser(authUser)).thenReturn(null)
        val actual = cut.writeAccess("SomethingTV", mock(HttpServletRequest::class.java))
        assertThat(actual.body!!).isFalse()
    }
}
