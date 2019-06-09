package com.cwtsite.cwt.domain.tetris.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.tetris.repository.entity.Tetris
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import java.sql.Timestamp

@DataTransferObject
data class TetrisDto(
        val highscore: Long,
        val user: UserMinimalDto?,
        val created: Timestamp) {

    companion object {

        fun toDto(tetris: Tetris) : TetrisDto {
            if (tetris.user == null) {
                return TetrisDto(
                        highscore = tetris.highscore,
                        user = null,
                        created = tetris.created
                )
            } else {
                return TetrisDto(
                        highscore = tetris.highscore,
                        user = UserMinimalDto.toDto(tetris.user),
                        created = tetris.created
                )
            }
        }
    }
}