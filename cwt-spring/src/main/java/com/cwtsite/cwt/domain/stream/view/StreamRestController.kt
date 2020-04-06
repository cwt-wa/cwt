package com.cwtsite.cwt.domain.stream.view

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.domain.stream.entity.Stream
import com.cwtsite.cwt.domain.stream.service.StreamService
import com.cwtsite.cwt.domain.stream.view.model.StreamDto
import com.cwtsite.cwt.twitch.TwitchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Duration
import java.time.LocalDateTime


@RestController
@RequestMapping("api/stream")
class StreamRestController {

    @Autowired private lateinit var streamService: StreamService
    @Autowired private lateinit var twitchService: TwitchService

    @Value("\${twitch.request-interval}") private val requestInterval: Int? = null

    @RequestMapping("", method = [RequestMethod.GET])
    fun queryAll(@RequestParam("new", defaultValue = "false") new: Boolean): ResponseEntity<List<StreamDto>> {
        if (!new || (twitchService.lastVideosRequest != null && Duration.between(twitchService.lastVideosRequest, LocalDateTime.now()).seconds < requestInterval!!)) {
            return ResponseEntity.ok(streamService.findAll().map { StreamDto.toDto(it) })
        }

        val allChannelsById = streamService.findAllChannels().associateBy { it.id }
        val newVideos = twitchService.requestVideos(allChannelsById.values.toList())
                .filter { it.hasCwtInTitle() }
                .map { StreamDto.toDto(it, allChannelsById[it.userId] ?: error("No channel with id ${it.userId}")) }
        streamService.saveStreams(newVideos.map { StreamDto.fromDto(it, allChannelsById[it.userId]!!) })

        return ResponseEntity.ok(newVideos)
    }

    @RequestMapping("/{id}", method = [RequestMethod.GET])
    fun getOne(@PathVariable("id") id: String): ResponseEntity<Stream> {
        return ResponseEntity.ok(
                streamService.findOne(id)
                        .orElseThrow { RestException("Stream not found.", HttpStatus.NOT_FOUND, null) })
    }
}

