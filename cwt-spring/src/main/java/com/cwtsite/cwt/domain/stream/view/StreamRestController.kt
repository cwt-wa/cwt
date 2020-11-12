package com.cwtsite.cwt.domain.stream.view

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.domain.game.service.GameService
import com.cwtsite.cwt.domain.game.view.model.GameDetailDto
import com.cwtsite.cwt.domain.playoffs.service.TreeService
import com.cwtsite.cwt.domain.stream.entity.Stream
import com.cwtsite.cwt.domain.stream.service.StreamService
import com.cwtsite.cwt.domain.stream.view.model.StreamDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("api/stream")
class StreamRestController {

    @Autowired private lateinit var streamService: StreamService
    @Autowired private lateinit var treeService: TreeService
    @Autowired private lateinit var gameService: GameService

    @RequestMapping("", method = [RequestMethod.GET])
    fun queryAll(): ResponseEntity<List<StreamDto>> =
            ResponseEntity.ok(streamService.findAll().sortedByDescending { it.createdAt }.map {
                StreamDto.toDto(
                        it,
                        when {
                            it.game?.playoff() ?: false -> GameDetailDto.localizePlayoffRound(
                                    it.game!!.tournament.threeWay!!,
                                    treeService.getNumberOfPlayoffRoundsInTournament(it.game!!.tournament),
                                    it.game!!.playoff!!.round)
                            else -> null
                        })
            })

    @RequestMapping("/{id}", method = [RequestMethod.GET])
    fun getOne(@PathVariable("id") id: String): ResponseEntity<Stream> {
        return ResponseEntity.ok(
                streamService.findOne(id)
                        .orElseThrow { RestException("Stream not found.", HttpStatus.NOT_FOUND, null) })
    }

    @PostMapping("{id}/game/{gameId}/link")
    fun linkGame(@PathVariable("id") streamId: String,
                 @PathVariable("gameId") gameId: Long): ResponseEntity<StreamDto> {
        val stream = streamService.findStream(streamId)
                .orElseThrow { throw RestException("There is no such stream.", HttpStatus.NOT_FOUND, null) }
        val game = gameService.findById(gameId)
                .orElseThrow { throw RestException("There is no such game.", HttpStatus.BAD_REQUEST, null) }
        return ResponseEntity.ok(StreamDto.toDto(streamService.associateGame(stream, game), null))
    }
}

