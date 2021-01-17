package com.cwtsite.cwt.domain.tournament.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.view.model.GameMinimalDto
import com.cwtsite.cwt.domain.group.entity.Group
import com.cwtsite.cwt.domain.group.entity.enumeration.GroupLabel

@DataTransferObject
data class GroupWithGamesDto(
        val id: Long,
        val label: GroupLabel,
        val tournament: TournamentDetailDto,
        var standings: List<StandingDto>,
        val games: List<GameMinimalDto>
) 

