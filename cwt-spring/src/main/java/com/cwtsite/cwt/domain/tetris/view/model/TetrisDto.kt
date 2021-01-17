package com.cwtsite.cwt.domain.tetris.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.tetris.repository.entity.Tetris
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import java.time.Instant

@DataTransferObject
data class TetrisDto(
        val highscore: Long,
        val user: UserMinimalDto?,
        val guestname : String?,
        val created: Instant?) 

