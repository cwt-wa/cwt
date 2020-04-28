package com.cwtsite.cwt.domain.tournament.view.controller

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.domain.game.service.GameService
import com.cwtsite.cwt.domain.game.view.model.GameCreationDto
import com.cwtsite.cwt.domain.group.service.GroupService
import com.cwtsite.cwt.domain.group.view.model.GroupDto
import com.cwtsite.cwt.domain.playoffs.service.PlayoffService
import com.cwtsite.cwt.domain.playoffs.service.TreeService
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.tournament.view.model.*
import com.cwtsite.cwt.domain.user.repository.entity.AuthorityRole
import com.cwtsite.cwt.domain.user.service.UserService
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import com.cwtsite.cwt.entity.Application
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("api/tournament")
class TournamentRestController @Autowired
constructor(private val tournamentService: TournamentService, private val userService: UserService,
            private val groupService: GroupService, private val playoffService: PlayoffService,
            private val gameService: GameService, private val treeService: TreeService) {

    @RequestMapping("/current", method = [RequestMethod.GET])
    fun findCurrentTournament(): ResponseEntity<TournamentDetailDto> =
            when (val currentTournament = tournamentService.getCurrentTournament()) {
                null -> ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                else -> ResponseEntity.ok(TournamentDetailDto.toDto(currentTournament))
            }

    @RequestMapping("/current/applications", method = [RequestMethod.GET])
    fun getApplicantsOfCurrentTournament(): ResponseEntity<List<Application>> {
        val currentTournament = tournamentService.getCurrentTournament()
                ?: throw RestException("There's no tournament currently.", HttpStatus.BAD_REQUEST, null)
        return ResponseEntity.ok(tournamentService.getApplicants(currentTournament))
    }

    @RequestMapping("current/group", method = [RequestMethod.GET])
    fun getGroupsForCurrentTournament(): ResponseEntity<List<GroupWithGamesDto>> {
        val (id) = tournamentService.getCurrentTournament()
                ?: throw RestException("There's no tournament currently.", HttpStatus.BAD_REQUEST, null)
        return getGroupsForTournament(id!!)
    }

    @RequestMapping("current/game/playoff", method = [RequestMethod.GET])
    fun getPlayoffGamesOfCurrentTournament(
            @RequestParam("voidable", defaultValue = "false") voidable: Boolean): ResponseEntity<List<PlayoffGameDto>> {
        val currentTournament = tournamentService.getCurrentTournament()

                ?: throw RestException("There's no tournament currently.", HttpStatus.BAD_REQUEST, null)
        if (voidable) return ResponseEntity.ok(treeService.getVoidablePlayoffGames().map { PlayoffGameDto.toDto(it) })
        return ResponseEntity.ok(playoffService.getGamesOfTournament(currentTournament).map { PlayoffGameDto.toDto(it) })
    }

    @RequestMapping("", method = [RequestMethod.GET])
    fun getAllTournaments(): ResponseEntity<List<TournamentDetailDto>> =
            ResponseEntity.ok(tournamentService.getAll().map { TournamentDetailDto.toDto(it) })

    @RequestMapping("/archive", method = [RequestMethod.GET])
    fun getTournamentsForArchive(): ResponseEntity<List<TournamentDto>> =
            ResponseEntity.ok(tournamentService.getAllFinished()
                    .map { TournamentDto.toDto(it) }
                    .sortedByDescending { it.year })

    @RequestMapping("", method = [RequestMethod.POST])
    @Secured(AuthorityRole.ROLE_ADMIN)
    fun createTournament(
            request: HttpServletRequest,
            @RequestBody startNewTournamentDto: StartNewTournamentDto): ResponseEntity<TournamentDetailDto> {
        try {
            return ResponseEntity.ok(TournamentDetailDto.toDto(
                    tournamentService.startNewTournament(startNewTournamentDto.moderatorIds)))
        } catch (e: IllegalStateException) {
            throw RestException("There are other unfinished tournaments.", HttpStatus.BAD_REQUEST, e)
        }
    }

    @RequestMapping("/{idOrYear}", method = [RequestMethod.GET])
    fun getTournament(@PathVariable("idOrYear") idOrYear: Long): ResponseEntity<TournamentDetailDto> {
        val tournament = if (idOrYear.toString().startsWith("20"))
            tournamentService.getTournamentByYear(idOrYear)
        else
            tournamentService.getTournament(idOrYear)

        return ResponseEntity.ok(
                TournamentDetailDto.toDto(tournament.orElseThrow { RestException("Tournament $idOrYear not found", HttpStatus.NOT_FOUND, null) }))
    }

    @RequestMapping("current/group/start", method = [RequestMethod.POST])
    @Secured(AuthorityRole.ROLE_ADMIN)
    fun startGroups(@RequestBody groupDtoList: List<GroupDto>): ResponseEntity<*> {
        val currentTournament = tournamentService.getCurrentTournament()
                ?: throw RestException("There's no tournament currently.", HttpStatus.BAD_REQUEST, null)

        val users = userService.getByIds(groupDtoList.flatMap { it.users })

        val groups = groupDtoList
                .map { groupDto ->
                    val groupMembers = users
                            .filter { (id1) ->
                                groupDto.users.stream()
                                        .anyMatch { userId -> userId == id1 }
                            }
                    GroupDto.map(currentTournament, groupMembers, groupDto.label)
                }

        return ResponseEntity.ok(groupService.startGroupStage(groups))
    }

    @RequestMapping("{id}/group", method = [RequestMethod.GET])
    fun getGroupsForTournament(@PathVariable("id") id: Long): ResponseEntity<List<GroupWithGamesDto>> {
        val tournament = tournamentService.getTournament(id)
                .orElseThrow { RestException("No such tournament.", HttpStatus.NOT_FOUND, null) }
        val games = gameService.findGroupGames(tournament)
        val groups = groupService.getGroupsForTournament(tournament)
                .onEach { groupService.sortStandings(it.standings, games.filter { game -> game.group == it }) }
                .map { GroupWithGamesDto.toDto(it, games.filter { game -> game.group == it }) }
        return ResponseEntity.ok(groups)
    }

    @RequestMapping("{id}/game/playoff", method = [RequestMethod.GET])
    fun getPlayoffGames(@PathVariable("id") id: Long): ResponseEntity<List<PlayoffGameDto>> {
        val tournament = tournamentService.getTournament(id)
                .orElseThrow { RestException("No such tournament", HttpStatus.NOT_FOUND, null) }
        return ResponseEntity.ok(playoffService.getGamesOfTournament(tournament, false).map { PlayoffGameDto.toDto(it) })
    }

    @RequestMapping("current/playoffs/start", method = [RequestMethod.POST])
    @Secured(AuthorityRole.ROLE_ADMIN)
    fun startPlayoffs(@RequestBody gameCreationDtoList: List<GameCreationDto>): ResponseEntity<List<PlayoffGameDto>> {
        val currentTournament = tournamentService.getCurrentTournament()
                ?: throw RestException("There's no tournament currently.", HttpStatus.BAD_REQUEST, null)
        val userIds = gameCreationDtoList.stream()
                .map { gameCreationDto -> listOf(gameCreationDto.homeUser, gameCreationDto.awayUser) }
                .reduce { longs, longs2 ->
                    val concatenatedLongs = ArrayList(longs)
                    concatenatedLongs.addAll(longs2)
                    concatenatedLongs
                }
                .orElseGet { emptyList() }
        val users = userService.getByIds(userIds)
        val games = gameCreationDtoList
                .map { dto ->
                    GameCreationDto.fromDto(
                            dto,
                            users.find { it.id == dto.homeUser } ?: throw RuntimeException(),
                            users.find { it.id == dto.awayUser } ?: throw RuntimeException(),
                            currentTournament
                    )
                }
        return ResponseEntity.ok(tournamentService.startPlayoffs(games).map { PlayoffGameDto.toDto(it) })
    }

    @RequestMapping("current/group/users")
    fun getGroupUsersOfCurrentTournament(): ResponseEntity<List<UserMinimalDto>> {
        val currentTournament = tournamentService.getCurrentTournament()
                ?: throw RestException("There is no tournament currently.", HttpStatus.BAD_REQUEST, null)

        return ResponseEntity.ok(groupService.getGroupsForTournament(currentTournament)
                .flatMap { it.standings.map { s -> s.user } }
                .map { UserMinimalDto.toDto(it) })
    }

    @RequestMapping("{id}", method = [RequestMethod.PUT])
    @Transactional
    @Secured(AuthorityRole.ROLE_ADMIN)
    fun updateTournament(@PathVariable("id") id: Long, @RequestBody dto: TournamentUpdateDto): ResponseEntity<TournamentDetailDto> {
        val tournament = tournamentService
                .getTournament(id)
                .orElseThrow { RestException("Tournament not found", HttpStatus.NOT_FOUND, null) }
        return ResponseEntity.ok(
                TournamentDetailDto.toDto(dto.update(tournament) { if (it == null) null else userService.getById(it).orElse(null) }))
    }

    @RequestMapping("{id}/group/users")
    fun getGroupUsersOfTournament(@PathVariable("id") tournamentId: Long): ResponseEntity<List<UserMinimalDto>> {
        return ResponseEntity.ok(groupService.getGroupsForTournament(
                tournamentService.getTournament(tournamentId)
                        .orElseThrow { RestException("Tournament $tournamentId not found", HttpStatus.NOT_FOUND, null) })
                .flatMap { it.standings.map { s -> s.user } }
                .map { UserMinimalDto.toDto(it) })
    }
}
