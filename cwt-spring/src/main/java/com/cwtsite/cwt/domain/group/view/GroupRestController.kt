package com.cwtsite.cwt.domain.group.view

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.domain.group.service.GroupService
import com.cwtsite.cwt.domain.group.view.model.ReplacePlayerDto
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.entity.AuthorityRole
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/group")
class GroupRestController {

    @Autowired private lateinit var groupService: GroupService
    @Autowired private lateinit var tournamentService: TournamentService

    @RequestMapping("")
    fun getAllUsers(): ResponseEntity<List<UserMinimalDto>> {
        val currentTournament = tournamentService.getCurrentTournament()
                ?: throw RestException("There's no tournament currently.", HttpStatus.BAD_REQUEST, null)
        return ResponseEntity.ok(groupService.getGroupsForTournament(currentTournament)
                .flatMap { it.standings.map { s -> s.user } }
                .map { UserMinimalDto.toDto(it) })
    }

    @RequestMapping("/replace")
    @Secured(AuthorityRole.ROLE_ADMIN)
    fun replacePlayer(@RequestBody dto: ReplacePlayerDto): ResponseEntity<Void> {
        val currentTournament = tournamentService.getCurrentTournament()
                ?: throw RestException("There's no tournament currently.", HttpStatus.BAD_REQUEST, null)
        if (currentTournament.status != TournamentStatus.GROUP) {
            throw RestException("The tournament is not in group stage.", HttpStatus.BAD_REQUEST, null)
        }
        groupService.replacePlayer(dto.toBeReplaced, dto.replacement)
        return ResponseEntity.ok().build()
    }
}

