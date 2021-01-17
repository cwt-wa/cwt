package com.cwtsite.cwt.domain.tournament.view.mapper

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.view.mapper.GameMinimalMapper
import com.cwtsite.cwt.domain.group.entity.Group
import com.cwtsite.cwt.domain.tournament.view.model.GroupWithGamesDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class GroupWithGamesMapper {

    @Autowired private lateinit var tournamentDetailMapper: TournamentDetailMapper
    @Autowired private lateinit var standingMapper: StandingMapper
    @Autowired private lateinit var gameMinimalMapper: GameMinimalMapper

    fun toDto(group: Group, games: List<Game>): GroupWithGamesDto = GroupWithGamesDto(
        id = group.id!!,
        label = group.label!!,
        tournament = tournamentDetailMapper.toDto(group.tournament!!),
        standings = group.standings.map { standingMapper.toDto(it) },
        games = games.map { gameMinimalMapper.toDto(it) }
    )
}
