package com.cwtsite.cwt.domain.game.view

import com.cwtsite.cwt.domain.core.view.model.PageDto
import com.cwtsite.cwt.domain.game.service.GameService
import com.cwtsite.cwt.domain.tournament.view.model.MapDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/map")
class MapRestController {

    @Autowired private lateinit var gameService: GameService

    @GetMapping("")
    fun retrieveMaps(
        @RequestParam("texture", required = false) texture: String?,
        @RequestParam("size", defaultValue = "10") size: Int,
        @RequestParam("start", defaultValue = "1") start: Int
    ): ResponseEntity<PageDto<MapDto>> {
        val maps = gameService.findMaps(start, size, texture)
            .map { stat -> MapDto.toDto(stat.game!!, stat.map!!, stat.texture) }
        return ResponseEntity.ok(PageDto.toDto<MapDto>(maps, emptyList()))
    }

    @GetMapping("texture")
    fun retrieveTextures(): ResponseEntity<Map<String, Long>> {
        return ResponseEntity.ok(gameService.countTextures())
    }
}
