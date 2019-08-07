package com.cwtsite.cwt.domain.tournament.view.controller

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.service.GameService
import com.cwtsite.cwt.domain.game.view.model.GameCreationDto
import com.cwtsite.cwt.domain.group.service.GroupService
import com.cwtsite.cwt.domain.group.view.model.GroupDto
import com.cwtsite.cwt.domain.playoffs.service.PlayoffService
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.tournament.view.model.GroupWithGamesDto
import com.cwtsite.cwt.domain.tournament.view.model.PlayoffGameDto
import com.cwtsite.cwt.domain.tournament.view.model.StartNewTournamentDto
import com.cwtsite.cwt.domain.tournament.view.model.TournamentUpdateDto
import com.cwtsite.cwt.domain.user.service.UserService
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import com.cwtsite.cwt.entity.Application
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("api/tournament")
class TournamentRestController @Autowired
constructor(private val tournamentService: TournamentService, private val userService: UserService,
            private val groupService: GroupService, private val playoffService: PlayoffService,
            private val gameService: GameService) {

    @RequestMapping("/current", method = [RequestMethod.GET])
    fun findCurrentTournament(): ResponseEntity<Tournament> {
        return ResponseEntity.ok(tournamentService.getCurrentTournament())
    }

    @RequestMapping("/current/applications", method = [RequestMethod.GET])
    fun getApplicantsOfCurrentTournament(): ResponseEntity<List<Application>> {
        val tournament = tournamentService.getCurrentTournament()
        return ResponseEntity.ok(tournamentService.getApplicants(tournament))
    }

    @RequestMapping("current/group", method = [RequestMethod.GET])
    fun getGroupsForCurrentTournament(): ResponseEntity<List<GroupWithGamesDto>> {
        val (id) = tournamentService.getCurrentTournament()
        return getGroupsForTournament(id!!)
    }

    @RequestMapping("current/game/playoff", method = [RequestMethod.GET])
    fun getPlayoffGamesOfCurrentTournament(): ResponseEntity<List<PlayoffGameDto>> {
        val currentTournament = try {
            tournamentService.getCurrentTournament()
        } catch (e: RuntimeException) {
            throw RestException("There is currently no tournament.", HttpStatus.BAD_REQUEST, e)
        }

        return ResponseEntity.ok(playoffService.getGamesOfTournament(currentTournament).map { PlayoffGameDto.toDto(it) })
    }

    @RequestMapping("", method = [RequestMethod.GET])
    fun getAllTournaments(): ResponseEntity<List<Tournament>> = ResponseEntity.ok(tournamentService.getAll())

    @RequestMapping("", method = [RequestMethod.POST])
    fun createTournament(request: HttpServletRequest, @RequestBody startNewTournamentDto: StartNewTournamentDto): Tournament {
        try {
            return tournamentService.startNewTournament(startNewTournamentDto.moderatorIds)
        } catch (e: IllegalStateException) {
            throw RestException("There are other unfinished tournaments.", HttpStatus.BAD_REQUEST, e)
        }
    }

    @RequestMapping("/{idOrYear}", method = [RequestMethod.GET])
    fun getTournament(@PathVariable("idOrYear") idOrYear: Long): ResponseEntity<Tournament> {
        val tournament = if (idOrYear.toString().startsWith("20"))
            tournamentService.getTournamentByYear(idOrYear)
        else
            tournamentService.getTournament(idOrYear)

        return ResponseEntity.ok(
                tournament.orElseThrow { RestException("Tournament $idOrYear not found", HttpStatus.NOT_FOUND, null) })
    }

    @RequestMapping("{id}/group/many", method = [RequestMethod.POST])
    fun addManyGroups(@PathVariable("id") id: Long, @RequestBody groupDtoList: List<GroupDto>): ResponseEntity<*> {
        val tournament = tournamentService.getTournament(id)

        if (!tournament.isPresent) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build<Any>()
        }

        val users = userService.getByIds(groupDtoList.flatMap { it.users })

        val groups = groupDtoList
                .map { groupDto ->
                    val groupMembers = users
                            .filter { (id1) ->
                                groupDto.users.stream()
                                        .anyMatch { userId -> userId == id1 }
                            }
                    GroupDto.map(tournament.get(), groupMembers, groupDto.label)
                }

        return ResponseEntity.ok(groupService.startGroupStage(tournament.get(), groups))
    }

    @RequestMapping("current/group/many", method = [RequestMethod.POST])
    fun addManyGroups(@RequestBody groupDtoList: List<GroupDto>): ResponseEntity<*> {
        // TODO Change tournament status
        val (id) = tournamentService.getCurrentTournament()
        return addManyGroups(id!!, groupDtoList)
    }

    @RequestMapping("{id}/group", method = [RequestMethod.GET])
    fun getGroupsForTournament(@PathVariable("id") id: Long): ResponseEntity<List<GroupWithGamesDto>> {
        val tournament = tournamentService.getTournament(id)
                .orElseThrow { RestException("No such tournament.", HttpStatus.NOT_FOUND, null) }
        val games = gameService.findGroupGames(tournament)
        val groups = groupService.getGroupsForTournament(tournament)
        return ResponseEntity.ok(groups.map { GroupWithGamesDto.toDto(it, games.filter { game -> game.group == it }) })
    }

    @RequestMapping("{id}/game/playoff", method = [RequestMethod.GET])
    fun getPlayoffGames(@PathVariable("id") id: Long): ResponseEntity<List<Game>> {
        return tournamentService.getTournament(id)
                .map { t -> ResponseEntity.ok(playoffService.getGamesOfTournament(t)) }
                .orElseGet { ResponseEntity.status(HttpStatus.NOT_FOUND).build() }
    }

    @RequestMapping("current/playoffs/start", method = [RequestMethod.POST])
    fun startPlayoffs(@RequestBody gameCreationDtoList: List<GameCreationDto>): ResponseEntity<List<Game>> {
        val userIds = gameCreationDtoList.stream()
                .map { gameCreationDto -> Arrays.asList(gameCreationDto.homeUser, gameCreationDto.awayUser) }
                .reduce { longs, longs2 ->
                    val concatenatedLongs = ArrayList(longs)
                    concatenatedLongs.addAll(longs2)
                    concatenatedLongs
                }
                .orElseGet { emptyList() }

        val currentTournament = tournamentService.getCurrentTournament()
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

        return ResponseEntity.ok(tournamentService.startPlayoffs(games))
    }

    @RequestMapping("current/group/users")
    fun getGroupUsersOfCurrentTournament(): ResponseEntity<List<UserMinimalDto>> {
        return ResponseEntity.ok(groupService.getGroupsForTournament(tournamentService.getCurrentTournament())
                .flatMap { it.standings.map { s -> s.user } }
                .map { UserMinimalDto.toDto(it) })
    }

    @RequestMapping("{id}", method = [RequestMethod.PUT])
    @Transactional
    fun updateTournament(@PathVariable("id") id: Long, @RequestBody dto: TournamentUpdateDto): ResponseEntity<Tournament> {
        val tournament = tournamentService.getTournament(id).orElseThrow { RestException("Tournament not found", HttpStatus.NOT_FOUND, null) }
        return ResponseEntity.ok(dto.update(tournament) { if (it == null) null else userService.getById(it).orElse(null) })
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
