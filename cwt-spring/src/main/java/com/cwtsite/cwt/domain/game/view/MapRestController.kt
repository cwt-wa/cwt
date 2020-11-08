package com.cwtsite.cwt.domain.game.view

import com.cwtsite.cwt.domain.game.service.GameService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/map")
class MapRestController {

  @Autowired private lateinit var gameService: GameService

  @GetMapping("")
  fun retrieveTextures(): ResponseEntity<List<String>> {
    return ResponseEntity.ok(gameService.retrieveDistinctTextures())
  }

}
