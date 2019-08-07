package com.cwtsite.cwt.domain.stream.view

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.domain.stream.service.StreamService
import com.cwtsite.cwt.domain.stream.view.model.ChannelCreationDto
import com.cwtsite.cwt.domain.stream.view.model.ChannelDto
import com.cwtsite.cwt.domain.user.service.UserService
import com.cwtsite.cwt.twitch.TwitchService
import com.cwtsite.cwt.twitch.model.TwitchUserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


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
}

