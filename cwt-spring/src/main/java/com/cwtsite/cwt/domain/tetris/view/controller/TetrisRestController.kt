package com.cwtsite.cwt.domain.tetris.view.controller

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.domain.tetris.repository.entity.Tetris
import com.cwtsite.cwt.domain.tetris.service.TetrisService
import com.cwtsite.cwt.domain.tetris.view.model.TetrisDto
import com.cwtsite.cwt.domain.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/tetris")
class TetrisRestController @Autowired
constructor(private val tetrisService: TetrisService, private val userService: UserService) {

    @RequestMapping("", method = [RequestMethod.POST])
    fun saveTetris(@RequestBody tetrisDto: TetrisDto): ResponseEntity<TetrisDto> {
        if (tetrisDto.guestname != null && !userService.validateUsername(tetrisDto.guestname)) throw RestException("Invalid Username.", HttpStatus.BAD_REQUEST, null)
        return ResponseEntity.ok(TetrisDto.toDto(tetrisService.add(null, tetrisDto.highscore, tetrisDto.guestname)))
    }

    @RequestMapping("", method = [RequestMethod.GET])
    fun get(): ResponseEntity<List<Tetris>> = ResponseEntity.ok(tetrisService.findAll())

}
