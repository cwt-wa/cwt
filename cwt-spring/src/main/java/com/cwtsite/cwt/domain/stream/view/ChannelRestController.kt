package com.cwtsite.cwt.domain.stream.view

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.stream.service.StreamService
import com.cwtsite.cwt.domain.stream.view.model.ChannelCreationDto
import com.cwtsite.cwt.domain.stream.view.model.ChannelDto
import com.cwtsite.cwt.domain.stream.view.model.StreamDto
import com.cwtsite.cwt.domain.user.service.AuthService
import com.cwtsite.cwt.domain.user.service.UserService
import com.cwtsite.cwt.twitch.TwitchService
import com.cwtsite.cwt.twitch.model.TwitchUserDto
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import kotlin.time.ExperimentalTime
import kotlin.time.minutes


@RestController
@RequestMapping("api/channel")
class ChannelRestController {

    @Autowired private lateinit var streamService: StreamService
    @Autowired private lateinit var twitchService: TwitchService
    @Autowired private lateinit var userService: UserService
    @Autowired private lateinit var authService: AuthService

    private val logger = LoggerFactory.getLogger(this::class.java)

    @RequestMapping("", method = [RequestMethod.POST])
    fun saveChannel(@RequestBody dto: ChannelCreationDto): ResponseEntity<ChannelDto> {
        val user = userService.getById(dto.user).orElseThrow { RestException("Invalid user.", HttpStatus.BAD_REQUEST, null) }
        if (streamService.findChannelByUser(user) != null) throw RestException("You already own a channel.", HttpStatus.BAD_REQUEST, null)
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
                val existingStreams = streamService.findStreams(channel).map { it.id }
                val newStreams = videos
                        .filter { !existingStreams.contains(it.id) }
                        .map { StreamDto.fromDto(it, channel) }
                val savedNewStreams = streamService.saveStreams(newStreams)
                savedNewStreams.forEach { stream ->
                    streamService.findMatchingGame(stream)?.also { game ->
                        streamService.associateGame(stream, game)
                    }
                }
                return@launch
            }
        }
    }

    @GetMapping("/{channelLogin}/auto-active")
    fun autoActive(@PathVariable("channelLogin") channelLogin: String): ResponseEntity<Boolean> {
        return streamService.findChannelByLogin(channelLogin)
                .map { ResponseEntity.ok(it.botAutoJoin) }
                .orElseThrow { RestException("Channel not found.", HttpStatus.NOT_FOUND, null) }
    }

    @PutMapping("/{channelId}/botAutoJoin")
    fun botAutoJoin(@PathVariable("channelId") channelId: String,
                    @RequestBody botAutoJoin: Map<String, Boolean>): ResponseEntity<ChannelDto> {
        val value = botAutoJoin["botAutoJoin"] ?: throw RestException("No value", HttpStatus.BAD_REQUEST, null)
        val channel = streamService.findChannel(channelId)
                .orElseThrow { RestException("Channel not found.", HttpStatus.NOT_FOUND, null) }
        return ResponseEntity.ok(ChannelDto.toDto(streamService.botAutoJoin(channel, value)))
    }


    @GetMapping("/{channelLogin}/write-access")
    fun writeAccess(@PathVariable("channelLogin") channelLogin: String,
                    request: HttpServletRequest): ResponseEntity<Boolean> {
        val user = authService.authUser(request) ?: return ResponseEntity.ok(false)
        logger.info("checking with channel of login \"${channelLogin}\" belonging to user $user")
        val channel = streamService.findChannelByUser(user) ?: return ResponseEntity.ok(false)
        logger.info("checking with channel \"${channelLogin}\" belonging to user $user")
        val result = channelLogin.toLowerCase() == channel.login!!.toLowerCase() && channel.user.id === user.id
        logger.info("result is $result")
        return ResponseEntity.ok(result)
    }
}
