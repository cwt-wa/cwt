package com.cwtsite.cwt.domain.stream.view

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.core.TwitchScheduler
import com.cwtsite.cwt.domain.game.service.GameService
import com.cwtsite.cwt.domain.stream.entity.Stream
import com.cwtsite.cwt.domain.stream.service.StreamService
import com.cwtsite.cwt.domain.stream.view.model.StreamDto
import com.cwtsite.cwt.domain.user.repository.entity.AuthorityRole
import com.cwtsite.cwt.twitch.TwitchService

import java.util.Optional

import org.slf4j.LoggerFactory

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/stream")
class StreamRestController {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Autowired private lateinit var streamService: StreamService
    @Autowired private lateinit var gameService: GameService
    @Autowired private lateinit var twitchService: TwitchService
    @Autowired private lateinit var twitchScheduler: TwitchScheduler

    @RequestMapping("", method = [RequestMethod.GET])
    fun queryAll(): ResponseEntity<List<StreamDto>> =
            ResponseEntity.ok(
                    streamService.findAll()
                            .sortedByDescending { it.createdAt }
                            .map { StreamDto.toDto(it) })

    @RequestMapping("/{id}", method = [RequestMethod.GET])
    fun getOne(@PathVariable("id") id: String): ResponseEntity<Stream> {
        return ResponseEntity.ok(
                streamService.findOne(id)
                        .orElseThrow { RestException("Stream not found.", HttpStatus.NOT_FOUND, null) })
    }

    @PostMapping("{id}/game/{gameId}/link")
    @Secured(AuthorityRole.ROLE_ADMIN)
    fun linkGame(@PathVariable("id") videoId: String,
                 @PathVariable("gameId") gameId: Long): ResponseEntity<StreamDto> {
        val game = gameService.findById(gameId)
                .orElseThrow { throw RestException("There is no such game.", HttpStatus.BAD_REQUEST, null) }
        val stream = streamService.findStream(videoId)
                .orElseGet {
                    logger.info("Video $videoId is not in CWT database.")
                    val streamFromTwitch = twitchService.requestVideo(videoId) ?: return@orElseGet null
                    val channel = streamService.findChannel(streamFromTwitch.userId)
                            .orElseThrow { throw RestException(
                                    "Channel ${streamFromTwitch.userId} of video is not registered.",
                                    HttpStatus.BAD_REQUEST, null) }
                    val mapped = StreamDto.fromDto(StreamDto.toDto(streamFromTwitch, channel), channel)
                    return@orElseGet streamService.saveStream(mapped)
                } ?: throw RestException("Twitch video could not be found.", HttpStatus.BAD_REQUEST, null)
        stream.game = game
        val saved = streamService.saveStream(stream)
        return ResponseEntity.ok(StreamDto.toDto(saved))
    }

    @DeleteMapping("{id}/link")
    @Secured(AuthorityRole.ROLE_ADMIN)
    fun unlinkGame(@PathVariable("id") id: String) : ResponseEntity<StreamDto> {
        val stream = streamService.findStream(id)
                .orElseThrow { throw RestException("No such stream.", HttpStatus.BAD_REQUEST, null) }
        stream.game = null
        return ResponseEntity.ok(StreamDto.toDto(streamService.saveStream(stream)))
    }

    @PostMapping("linking")
    fun linkStreams(): ResponseEntity<List<StreamDto>> =
            ResponseEntity.ok(streamService.link().map { StreamDto.toDto(it) })

    @PostMapping("job")
    fun runJob(): ResponseEntity<Unit> {
        twitchScheduler.schedule()
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @DeleteMapping("/{id}")
    @Secured(AuthorityRole.ROLE_ADMIN)
    fun deleteStream(@PathVariable("id") id: String): ResponseEntity<Void> {
        val stream = streamService.findStream(id)
                .orElseThrow { throw RestException("No such stream.", HttpStatus.BAD_REQUEST, null) }
        streamService.deleteStream(stream)
        return ResponseEntity.status(HttpStatus.OK).build()
    }
}

