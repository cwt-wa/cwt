package com.cwtsite.cwt.domain.stream.view

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.stream.service.StreamService
import com.cwtsite.cwt.domain.stream.view.model.ChannelCreationDto
import com.cwtsite.cwt.domain.stream.view.model.ChannelDto
import com.cwtsite.cwt.domain.stream.view.model.StreamDto
import com.cwtsite.cwt.domain.user.service.UserService
import com.cwtsite.cwt.twitch.TwitchService
import com.cwtsite.cwt.twitch.model.TwitchUserDto
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.time.ExperimentalTime
import kotlin.time.minutes


@RestController
@RequestMapping("api/channel")
class ChannelRestController {

    @Autowired private lateinit var streamService: StreamService
    @Autowired private lateinit var twitchService: TwitchService
    @Autowired private lateinit var userService: UserService

    @RequestMapping("", method = [RequestMethod.POST])
    fun saveChannel(@RequestBody dto: ChannelCreationDto): ResponseEntity<ChannelDto> {
        val user = userService.getById(dto.user).orElseThrow { RestException("Invalid user.", HttpStatus.BAD_REQUEST, null) }
        if (streamService.findChannelByUsers(listOf(user)).isNotEmpty()) throw RestException("You already own a channel.", HttpStatus.BAD_REQUEST, null)
        val usersFromTwitch = twitchService.requestUsers(dto.twitchLoginName)
        if (usersFromTwitch.isEmpty()) throw RestException("Channel was not found at Twitch.", HttpStatus.BAD_REQUEST, null)
        if (streamService.findChannel(usersFromTwitch[0].id!!).isPresent) throw RestException("Channel already registered.", HttpStatus.BAD_REQUEST, null)
        return ResponseEntity.ok(ChannelDto.toDto(streamService.saveChannel(TwitchUserDto.fromDto(usersFromTwitch[0], user, dto.title))))
    }

    @RequestMapping("", method = [RequestMethod.GET])
    fun queryChannel(@RequestParam("user") users: List<Long>?): ResponseEntity<List<ChannelDto>> {
        return when (users) {
            null -> ResponseEntity.ok(streamService.findAllChannels().map { ChannelDto.toDto(it) })
            else -> ResponseEntity.ok(streamService.findChannelByUsers(
                    userService.findByIds(*users.toLongArray())).map { ChannelDto.toDto(it) })
        }
    }

    @ExperimentalTime
    @PostMapping("ping/{twitchUserId}")
    fun saveStream(@PathVariable("twitchUserId") twitchUserId: String): ResponseEntity<Unit> {
        val channel = streamService.findChannel(twitchUserId)
                .orElseThrow { throw RestException("I don't know this Twitch user.", HttpStatus.BAD_REQUEST, null) }
        pollForVideo(channel, listOf(0, 1, 3, 6, 10, 20))
        return ResponseEntity.ok().build<Unit>()
    }

    @ExperimentalTime
    fun pollForVideo(channel: Channel, intervals: List<Int>) = GlobalScope.launch {
        val channels = listOf(channel)
        for (interval in intervals) {
            delay(interval.minutes.toLongMilliseconds())
            val videos = twitchService.requestVideos(channels)
                    .filter { it.hasCwtInTitle() }
                    .map { StreamDto.toDto(it, channel) }
            if (videos.isNotEmpty()) {
                val streams = streamService.saveStreams(videos.map { StreamDto.fromDto(it, channel) })
                streams.forEach { stream ->
                    streamService.findMatchingGame(stream)?.let { game ->
                        streamService.associateGame(stream, game)
                    }
                }
                return@launch
            }
        }
    }
}
