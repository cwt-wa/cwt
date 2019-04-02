package com.cwtsite.cwt.domain.user.view.controller

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.domain.application.service.ApplicationService
import com.cwtsite.cwt.domain.core.view.model.PageDto
import com.cwtsite.cwt.domain.group.service.GroupService
import com.cwtsite.cwt.domain.playoffs.service.PlayoffService
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.AuthService
import com.cwtsite.cwt.domain.user.service.UserService
import com.cwtsite.cwt.domain.user.view.model.*
import com.cwtsite.cwt.entity.Application
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("api/user")
class UserRestController @Autowired
constructor(private val userService: UserService, private val applicationService: ApplicationService,
            private val authService: AuthService, private val tournamentService: TournamentService,
            private val groupService: GroupService, private val playoffService: PlayoffService) {

    @RequestMapping("", method = [RequestMethod.GET])
    fun findAll(@RequestParam("term") term: String?): ResponseEntity<List<User>> {
        if (term == null) {
            return ResponseEntity.ok(userService.findAllOrderedByUsername())
        }

        return ResponseEntity.ok(userService.findByUsernameContaining(term))
    }

    @RequestMapping("/{id}/can-apply", method = [RequestMethod.GET])
    fun userCanApplyForTournament(@PathVariable("id") id: Long): ResponseEntity<Boolean> {
        return ResponseEntity.ok(this.userService.userCanApplyForCurrentTournament(assertUser(id)))
    }

    @RequestMapping("/{usernameOrId}", method = [RequestMethod.GET])
    fun getOne(@PathVariable("usernameOrId") usernameOrId: Any): ResponseEntity<UserDetailDto> {
        return if (usernameOrId is String) {
            val user = userService.findByUsername(usernameOrId)
            ResponseEntity.ok(UserDetailDto.toDto(
                    user, UserStatsDto.toDtos(user.userStats?.timeline ?: userService.createDefaultUserStatsTimeline())))
        } else {
            usernameOrId as Long
            val user = assertUser(usernameOrId)
            ResponseEntity.ok(UserDetailDto.toDto(
                    user,
                    UserStatsDto.toDtos(user.userStats?.timeline ?: userService.createDefaultUserStatsTimeline())))
        }
    }

    @RequestMapping("/still-in-tournament", method = [RequestMethod.GET])
    fun usersWhoAreStillInTournamentAndCanReportGames(): ResponseEntity<List<UserMinimalDto>> {
        val currentTournament = tournamentService.getCurrentTournament()

        val users = when {
            currentTournament.status == TournamentStatus.GROUP ->
                groupService.findAllGroupMembers(currentTournament)
            currentTournament.status == TournamentStatus.PLAYOFFS ->
                playoffService.getGamesOfTournament(currentTournament)
                        .flatMap { listOf(it.homeUser, it.awayUser) }
                        .filterNotNull()
                        .distinct()
            else ->
                emptyList()
        }

        return ResponseEntity.ok(
                users
                        .filter { userService.userCanReportForCurrentTournament(it) }
                        .map { UserMinimalDto.toDto(it) })
    }

    @RequestMapping("/{id}/can-report", method = [RequestMethod.GET])
    fun userCanReportForCurrentTournament(@PathVariable("id") id: Long): ResponseEntity<Boolean> {
        return ResponseEntity.ok(this.userService.userCanReportForCurrentTournament(assertUser(id)))
    }

    @RequestMapping("/{id}/application", method = [RequestMethod.POST])
    fun applyForTournament(@PathVariable("id") id: Long): ResponseEntity<Application> {
        return ResponseEntity.ok(this.applicationService.apply(assertUser(id)))
    }

    @RequestMapping("/{id}/remaining-opponents", method = [RequestMethod.GET])
    fun getRemainingOpponents(@PathVariable("id") id: Long): ResponseEntity<List<User>> {
        val user = assertUser(id)
        return ResponseEntity.ok(userService.getRemainingOpponents(user))
    }

    @RequestMapping("/page", method = [RequestMethod.GET])
    fun queryUsersPaged(pageDto: PageDto<UserOverviewDto>): ResponseEntity<PageDto<UserOverviewDto>> {
        return ResponseEntity.ok(PageDto.toDto(
                userService.findPaginated(
                        pageDto.start, pageDto.size,
                        pageDto.asSortWithFallback(Sort.Direction.DESC, "userStats.trophyPoints"))
                        .map { user ->
                            UserOverviewDto.toDto(
                                    user,
                                    UserStatsDto.toDtos(user.userStats?.timeline ?: userService.createDefaultUserStatsTimeline())) },
                Arrays.asList(
                        "userStats.trophyPoints,Trophies",
                        "userStats.participations,Participations",
                        "username,Username",
                        "userProfile.country,Country")))
    }

    @RequestMapping("/{id}", method = [RequestMethod.POST])
    fun changeUser(@RequestBody userChangeDto: UserChangeDto,
                   @PathVariable("id") id: Long,
                   request: HttpServletRequest): ResponseEntity<UserChangeDto> {
        var user = assertUser(id)

        if (authService.getUserFromToken(request.getHeader(authService.tokenHeaderName)).id != user.id) {
            throw RestException("You are not allowed to change another user.", HttpStatus.BAD_REQUEST, null)
        }

        try {
            user = userService.changeUser(user, userChangeDto.about, userChangeDto.username, userChangeDto.country)
        } catch (e: UserService.InvalidUsernameException) {
            throw RestException("Username invalid.", HttpStatus.BAD_REQUEST, null)
        }

        return ResponseEntity.ok(UserChangeDto.toDto(user))
    }

    private fun assertUser(id: Long): User = userService.getById(id)
            .orElseThrow { RestException("User $id not found", HttpStatus.NOT_FOUND, null) }
}
