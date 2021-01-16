package com.cwtsite.cwt.domain.game.view.mapper

import com.cwtsite.cwt.domain.game.view.model.RatingDto
import com.cwtsite.cwt.domain.game.entity.Rating
import com.cwtsite.cwt.domain.game.entity.enumeration.RatingType
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import org.springframework.stereotype.Component
import org.springframework.beans.factory.annotation.Autowired

@Component
class RatingMapper {


    fun toDto(rating: Rating) = RatingDto(
            id = rating.id!!,
            user = UserMinimalDto(id = rating.user!!.id!!, username = rating.user!!.username),
            type = rating.type
    )
}

