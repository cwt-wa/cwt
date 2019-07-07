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
import com.cwtsite.cwt.domain.user.service.JwtTokenUtil
import com.cwtsite.cwt.domain.user.service.UserService
import com.cwtsite.cwt.domain.user.view.model.*
import com.cwtsite.cwt.entity.Application
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.data.domain.Sort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.*
import javax.security.auth.login.CredentialException
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("api/user")
class UserRestController @Autowired
constructor(private val userService: UserService, private val applicationService: ApplicationService,
            private val authService: AuthService, private val tournamentService: TournamentService,
            private val groupService: GroupService, private val playoffService: PlayoffService,
            private val jwtTokenUtil: JwtTokenUtil, private val userDetailsService: UserDetailsService) {

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
    fun getOne(@PathVariable("usernameOrId") usernameOrId: String): ResponseEntity<UserDetailDto> {
        val isId = try {
            usernameOrId.toLong()
            true
        } catch (e: NumberFormatException) {
            false
        }

        return if (!isId) {
            val user = userService.findByUsername(usernameOrId)
            ResponseEntity.ok(UserDetailDto.toDto(
                    user, UserStatsDto.toDtos(user.userStats?.timeline ?: userService.createDefaultUserStatsTimeline())))
        } else {
            val user = assertUser(usernameOrId.toLong())
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
                        "country.name,Country")))
    }

    @RequestMapping("/{id}", method = [RequestMethod.POST])
    fun changeUser(@RequestBody userChangeDto: UserChangeDto,
                   @PathVariable("id") id: Long,
                   request: HttpServletRequest): ResponseEntity<JwtAuthenticationResponse>? {
        var user = assertUser(id)
        val upcomingUsernameChange = userChangeDto.username != user.username

        if (authService.getUserFromToken(request.getHeader(authService.tokenHeaderName)).id != user.id) {
            throw RestException("You are not allowed to change another user.", HttpStatus.BAD_REQUEST, null)
        }

        try {
            user = userService.changeUser(
                    user, userChangeDto.about, userChangeDto.username,
                    if (userChangeDto.country != null) userService.findCountryById(userChangeDto.country).orElse(null) else null)

            if (upcomingUsernameChange) {
                val userDetails = userDetailsService.loadUserByUsername(user.username)
                val token = jwtTokenUtil.generateToken(userDetails)

                return ResponseEntity.ok<JwtAuthenticationResponse>(JwtAuthenticationResponse(token))
            }
        } catch (e: UserService.InvalidUsernameException) {
            throw RestException("Username invalid.", HttpStatus.BAD_REQUEST, null)
        } catch (e: UserService.UsernameTakenException) {
            throw RestException("Username already taken.", HttpStatus.BAD_REQUEST, null)
        }

        return null
    }

    @RequestMapping("/{id}/change-password", method = [RequestMethod.POST])
    fun changePassword(@RequestBody passwordChangeDto: PasswordChangeDto,
                       @PathVariable("id") id: Long,
                       request: HttpServletRequest) {
        if (authService.getUserFromToken(request.getHeader(authService.tokenHeaderName)).id != assertUser(id).id) {
            throw RestException("You are not allowed to change another user.", HttpStatus.BAD_REQUEST, null)
        }
        try {
            userService.changePassword(assertUser(id), passwordChangeDto.currentPassword, passwordChangeDto.newPassword)
        } catch (e: CredentialException) {
            throw RestException("Wrong password.", HttpStatus.BAD_REQUEST, e);
        }
    }

    @RequestMapping("{id}/change-photo", method = [RequestMethod.POST], consumes = ["multipart/form-data"])
    fun changePhoto(@RequestParam("photo") photo: MultipartFile, @PathVariable("id") userId: Long, request: HttpServletRequest) {
        val user = assertUser(userId)

        if (authService.getUserFromToken(request.getHeader(authService.tokenHeaderName)).id != user.id) {
            throw RestException("You are not allowed to change another user.", HttpStatus.BAD_REQUEST, null)
        }

        userService.changePhoto(user, photo)
    }

    @RequestMapping("/{userId}/photo", method = [RequestMethod.GET])
    @Throws(IOException::class)
    fun getUserPhoto(@PathVariable("userId") userId: Long): ResponseEntity<Resource> {
        val user = assertUser(userId)

        if (user.photo == null) {
            throw RestException("There is no photo from this user.", HttpStatus.NOT_FOUND, null)
        }

        val resource = ByteArrayResource(user.photo!!.file)

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${user.username}.${user.photo!!.extension}")
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType(user.photo!!.mediaType))
                .body(resource)
    }


    private fun assertUser(id: Long): User = userService.getById(id)
            .orElseThrow { RestException("User $id not found", HttpStatus.NOT_FOUND, null) }
}
