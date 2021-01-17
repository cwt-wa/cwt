package com.cwtsite.cwt.domain.game.view.model

import com.cwtsite.cwt.domain.game.entity.Rating
import com.cwtsite.cwt.domain.game.entity.enumeration.RatingType
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto

data class RatingDto(
        val id: Long,
        val user: UserMinimalDto,
        val type: RatingType
) 

