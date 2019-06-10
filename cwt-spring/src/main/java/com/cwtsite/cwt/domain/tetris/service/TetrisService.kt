package com.cwtsite.cwt.domain.tetris.service

import com.cwtsite.cwt.domain.tetris.repository.TetrisRepository
import com.cwtsite.cwt.domain.tetris.repository.entity.Tetris
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.sql.Timestamp

@Component
class TetrisService {

    @Autowired
    private lateinit var tetrisRepository: TetrisRepository

    fun add(user: User?, highscore: Long, created : Timestamp): Tetris =
            tetrisRepository.save(Tetris(user = user, highscore = highscore, created = created))

    fun findAll(): List<Tetris> = tetrisRepository.findAll()
}