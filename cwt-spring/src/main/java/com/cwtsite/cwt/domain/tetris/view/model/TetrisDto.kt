package com.cwtsite.cwt.domain.tetris.view.model

import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto

data class TetrisDto(
        val highscore: Long,
        val user: UserMinimalDto)