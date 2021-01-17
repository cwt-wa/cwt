package com.cwtsite.cwt.domain.game.view.model

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.PlayoffGame
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.user.repository.entity.User

data class GameCreationDto(
        var id: Long? = null,
        var homeUser: Long,
        var awayUser: Long,
        var playoff: PlayoffDto? = null
) 

