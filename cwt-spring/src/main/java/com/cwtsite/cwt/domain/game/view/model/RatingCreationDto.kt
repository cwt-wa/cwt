package com.cwtsite.cwt.domain.game.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.game.entity.enumeration.RatingType

@DataTransferObject
data class RatingCreationDto(
    val user: Long,
    val type: RatingType
)
