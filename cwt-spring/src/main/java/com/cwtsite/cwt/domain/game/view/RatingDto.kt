package com.cwtsite.cwt.domain.game.view

import com.cwtsite.cwt.domain.game.entity.Rating
import com.cwtsite.cwt.domain.game.entity.enumeration.RatingType
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto

data class RatingDto(
    val id: Long,
    val user: UserMinimalDto,
    val type: RatingType
) {

    companion object {
        fun toDto(rating: Rating) = RatingDto(
            id = rating.id!!,
            user = UserMinimalDto.toDto(rating.user),
            type = rating.type
        )
    }
}
