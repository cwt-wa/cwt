package com.cwtsite.cwt.domain.tetris.view.mapper

import com.cwtsite.cwt.domain.tetris.repository.entity.Tetris
import com.cwtsite.cwt.domain.tetris.view.model.TetrisDto
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import org.springframework.stereotype.Component

@Component
class TetrisMapper {

    fun toDto(tetris: Tetris): TetrisDto {
        return if (tetris.user == null) {
            TetrisDto(
                highscore = tetris.highscore,
                user = null,
                guestname = tetris.guestname,
                created = tetris.created
            )
        } else {
            TetrisDto(
                highscore = tetris.highscore,
                user = UserMinimalDto(id = tetris.user.id!!, username = tetris.user.username),
                guestname = null,
                created = tetris.created
            )
        }
    }
}

