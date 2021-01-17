package com.cwtsite.cwt.domain.tournament.view.mapper

import com.cwtsite.cwt.domain.tournament.view.model.StandingDto
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import com.cwtsite.cwt.entity.GroupStanding
import org.springframework.stereotype.Component

@Component
class StandingMapper {

    fun toDto(standing: GroupStanding) = StandingDto(
        id = standing.id!!,
        points = standing.points,
        games = standing.games,
        gameRatio = standing.gameRatio,
        roundRatio = standing.roundRatio,
        user = UserMinimalDto(id = standing.user.id!!, username = standing.user.username)
    )
}

