package com.cwtsite.cwt.domain.game.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import java.time.Instant

@DataTransferObject
data class GameMinimalDto(
        val id: Long,
        val scoreHome: Int,
        val scoreAway: Int,
        val techWin: Boolean,
        val created: Instant,
        val reportedAt: Instant,
        val modified: Instant,
        val homeUser: UserMinimalDto,
        val awayUser: UserMinimalDto,
        val replayExists: Boolean
) 

