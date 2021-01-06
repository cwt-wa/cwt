package com.cwtsite.cwt.core

import com.cwtsite.cwt.domain.stream.entity.Stream
import com.cwtsite.cwt.domain.stream.service.ChannelRepository
import com.cwtsite.cwt.domain.stream.service.StreamRepository
import com.cwtsite.cwt.domain.stream.service.StreamService
import com.cwtsite.cwt.test.EntityDefaults.channel
import com.cwtsite.cwt.test.EntityDefaults.game
import com.cwtsite.cwt.test.EntityDefaults.stream
import com.cwtsite.cwt.test.MockitoUtils.safeArgThat
import com.cwtsite.cwt.twitch.TwitchService
import com.cwtsite.cwt.twitch.model.TwitchVideoDto

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

import org.mockito.ArgumentMatcher
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class TwitchSchedulerTest {

    @InjectMocks private lateinit var twitchScheduler: TwitchScheduler
    @Mock private lateinit var channelRepository: ChannelRepository
    @Mock private lateinit var streamRepository: StreamRepository
    @Mock private lateinit var streamService: StreamService
    @Mock private lateinit var twitchService: TwitchService

    @Test
    fun schedule() {
        val channels = listOf(channel(id = "1"), channel(id = "2"), channel(id = "3"))
        `when`(channelRepository.findAll()).thenReturn(channels)
        val streams = listOf(
                stream(id = "10", channel = channels[0]),
                stream(id = "11", channel = channels[1]),
                stream(id = "12", channel = channels[2]),
                stream(id = "99", channel = channels[2])) // doesn't exist anymore
        `when`(streamRepository.findAll()).thenReturn(streams)
        val twitchVideoDtos = listOf(
                // existing
                createTwitchVideoDto(streams[0].id, channels[0].id),
                // existing, update information
                createTwitchVideoDto(streams[1].id, channels[1].id, title = "udpated"),
                // existing
                createTwitchVideoDto(streams[2].id, channels[2].id),
                // new
                createTwitchVideoDto("13", channels[2].id))
        `when`(twitchService.requestVideos(channels)).thenReturn(twitchVideoDtos)
        val newStream = stream(id = twitchVideoDtos[3].id)
        val associatedGame = game()
        `when`(streamService.findMatchingGame(newStream)).thenReturn(associatedGame)
        twitchScheduler.schedule()
        verify(streamRepository).deleteAll(listOf(streams[3]))
        verify(streamService).link()
        verify(streamRepository).saveAll(safeArgThat<List<Stream>>(object : ArgumentMatcher<List<Stream>> {
            override fun matches(argStreams: List<Stream>) =
                    argStreams.size == 1
                        && argStreams[0].id == twitchVideoDtos[3].id
                        && argStreams[0].game == associatedGame
        }))
    }

    private fun createTwitchVideoDto(id: String, userId: String, title: String = "original"): TwitchVideoDto {
        val twitchVideoDto = mock(TwitchVideoDto::class.java)
        `when`(twitchVideoDto.id).thenReturn(id)
        `when`(twitchVideoDto.userId).thenReturn(userId)
        `when`(twitchVideoDto.title).thenReturn(title)
        `when`(twitchVideoDto.hasCwtInTitle()).thenReturn(true)
        return twitchVideoDto
    }
}

