package com.cwtsite.cwt.domain.stream.view

import com.cwtsite.cwt.domain.stream.service.StreamService
import com.cwtsite.cwt.domain.stream.view.model.StreamDto
import com.cwtsite.cwt.twitch.TwitchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("api/stream")
class StreamRestController {

    @Autowired private lateinit var streamService: StreamService
    @Autowired private lateinit var twitchService: TwitchService

    @RequestMapping("", method = [RequestMethod.GET])
    fun queryAll(@RequestParam("new", defaultValue = "false") new: Boolean): ResponseEntity<List<StreamDto>> {
        if (!new) return ResponseEntity.ok(streamService.findAll().map { StreamDto.toDto(it) })

        val allChannelsById = streamService.findAllChannels().associateBy { it.id }
        val newVideos = twitchService.requestVideos(allChannelsById.keys.toList())
                .map { StreamDto.toDto(it, allChannelsById[it.userId] ?: error("No channel with id ${it.userId}")) }
        streamService.saveStreams(newVideos.map { StreamDto.fromDto(it, allChannelsById[it.userId] ?: error("No channel with id ${it.userId}")) })
        return ResponseEntity.ok(newVideos)
    }
}

