package com.cwtsite.cwt.domain.tetris.view

import com.cwtsite.cwt.domain.user.service.AuthService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("")
class TetrisRestController
constructor(private val authService: AuthService) {

    @RequestMapping("/{highscore}", method = [RequestMethod.POST])
    fun addUser(@PathVariable highscore: Int) {
        val username = authService.getUserFromToken(authService.tokenHeaderName).username;
    }
}