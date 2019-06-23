package com.cwtsite.cwt.domain.game.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject

@DataTransferObject
data class BetCreationDto(
        val user: Long,
        val game: Long,
        val betOnHome: Boolean
)
