package com.cwtsite.cwt.domain.tetris.view.controller

import com.cwtsite.cwt.domain.tetris.repository.entity.Tetris
import com.cwtsite.cwt.domain.tetris.service.TetrisService
import com.cwtsite.cwt.domain.tetris.view.model.TetrisDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.sql.Timestamp

@RestController
@RequestMapping("/api/tetris")
class TetrisRestController @Autowired
constructor(private val tetrisService: TetrisService) {

    @RequestMapping("", method = [RequestMethod.POST])
    fun saveTetris(@RequestBody tetrisDto: TetrisDto): ResponseEntity<TetrisDto> {
        val timestamp = Timestamp(System.currentTimeMillis());
        return ResponseEntity.ok(TetrisDto.toDto(tetrisService.add(null, tetrisDto.highscore, timestamp, tetrisDto.guestname)))
    }

    @RequestMapping("", method = [RequestMethod.GET])
    fun get(): ResponseEntity<List<Tetris>> = ResponseEntity.ok(tetrisService.findAll())

}