package com.cwtsite.cwt.domain.user.view.controller

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.domain.application.service.ApplicationService
import com.cwtsite.cwt.domain.core.view.model.PageDto
import com.cwtsite.cwt.domain.group.service.GroupService
import com.cwtsite.cwt.domain.playoffs.service.PlayoffService
import com.cwtsite.cwt.domain.tetris.service.TetrisService
import com.cwtsite.cwt.domain.tetris.view.model.TetrisDto
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.entity.AuthorityRole
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.AuthService
import com.cwtsite.cwt.domain.user.service.JwtTokenUtil
import com.cwtsite.cwt.domain.user.service.UserService
import com.cwtsite.cwt.domain.user.view.model.*
import com.cwtsite.cwt.entity.Application
import com.cwtsite.cwt.security.SecurityContextHolderFacade
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.data.domain.Sort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import javax.security.auth.login.CredentialException
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("api/user")
class UserRestController @Autowired
constructor(
    private val userService: UserService,
    private val applicationService: ApplicationService,
    private val authService: AuthService,
    private val tournamentService: TournamentService,
    private val groupService: GroupService,
    private val playoffService: PlayoffService,
    private val jwtTokenUtil: JwtTokenUtil,
    private val userDetailsService: UserDetailsService,
    private val tetrisService: TetrisService,
    private val authenticationManager: AuthenticationManager,
    private val securityContextHolderFacade: SecurityContextHolderFacade
) {

    @RequestMapping("", method = [RequestMethod.GET])
    fun findAll(@RequestParam("term") term: String?,
                @RequestParam("username") usernames: List<String>?,
                @RequestParam("minimal", defaultValue = "false") minimal: Boolean): ResponseEntity<List<*>> {
        val result =  when {
            term != null -> userService.findByUsernameContaining(term)
            usernames != null -> userService.findByUsernamesIgnoreCase(usernames)
            else -> userService.findAllOrderedByUsername()
        }
        if (!minimal) return ResponseEntity.ok(result)
        return ResponseEntity.ok(result.map { UserMinimalDto.toDto(it) })
    }

    @RequestMapping("/{id}/can-apply", method = [RequestMethod.GET])
    fun userCanApplyForTournament(@PathVariable("id") id: Long): ResponseEntity<Boolean> {
        return ResponseEntity.ok(this.userService.userCanApplyForCurrentTournament(assertUser(id)))
    }

    @RequestMapping("/{usernameOrId}", method = [RequestMethod.GET])
    fun getOne(
        @PathVariable("usernameOrId") usernameOrId: String,
        @RequestParam("include-email", defaultValue = "false") includeEmail: Boolean,
        request: HttpServletRequest
    ): ResponseEntity<UserDetailDto> {
        val isId = try {
            usernameOrId.toLong()
            true
        } catch (e: NumberFormatException) {
            false
        }

        val user = when {
            isId -> assertUser(usernameOrId.toLong())
            else -> userService.findByUsername(usernameOrId)
                ?: throw RestException("User not found.", HttpStatus.NOT_FOUND, null)
        }

        if (includeEmail) {
            if (authService.authUser(request)!!.id != user.id) {
                throw RestException("Email address inclusion forbidden.", HttpStatus.BAD_REQUEST, null)
            }
        }

        return ResponseEntity.ok(
            UserDetailDto.toDto(
                user,
                UserStatsDto.toDtos(user.userStats?.timeline ?: userService.createDefaultUserStatsTimeline())
            )
        )
    }

    @RequestMapping("/still-in-tournament", method = [RequestMethod.GET])
    fun usersWhoAreStillInTournamentAndCanReportGames(): ResponseEntity<List<UserMinimalDto>> {
        val currentTournament = tournamentService.getCurrentTournament()
        val users = when {
            currentTournament == null -> emptyList()
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
                .map { UserMinimalDto.toDto(it) }
        )
    }

    @RequestMapping("/{id}/can-report", method = [RequestMethod.GET])
    fun userCanReportForCurrentTournament(@PathVariable("id") id: Long): ResponseEntity<Boolean> {
        return ResponseEntity.ok(this.userService.userCanReportForCurrentTournament(assertUser(id)))
    }

    @RequestMapping("/{id}/application", method = [RequestMethod.POST])
    @Secured(AuthorityRole.ROLE_USER)
    fun applyForTournament(@PathVariable("id") id: Long, request: HttpServletRequest): ResponseEntity<Application> {
        tournamentService.getCurrentTournament()
            ?: throw RestException("There's no tournament currently.", HttpStatus.BAD_REQUEST, null)
        val userToApply = assertUser(id)
        val authUser = authService.authUser(request)
        if (authUser != userToApply && !authUser!!.isAdmin()) throw RestException("Forbidden.", HttpStatus.FORBIDDEN, null)
        try {
            return ResponseEntity.ok(this.applicationService.apply(userToApply))
        } catch (e: ApplicationService.AlreadyAppliedException) {
            throw RestException("You have already applied.", HttpStatus.BAD_REQUEST, e)
        }
    }

    @RequestMapping("/{id}/remaining-opponents", method = [RequestMethod.GET])
    fun getRemainingOpponents(@PathVariable("id") id: Long): ResponseEntity<List<User>> {
        val user = assertUser(id)
        return ResponseEntity.ok(userService.getRemainingOpponents(user))
    }

    @RequestMapping("/page", method = [RequestMethod.GET])
    fun queryUsersPaged(pageDto: PageDto<UserOverviewDto>): ResponseEntity<PageDto<UserOverviewDto>> {
        return ResponseEntity.ok(
            PageDto.toDto(
                userService.findPaginated(
                    pageDto.start, pageDto.size,
                    pageDto.asSortWithFallback(Sort.Direction.DESC, "userStats.trophyPoints")
                )
                    .map { user ->
                        UserOverviewDto.toDto(
                            user,
                            UserStatsDto.toDtos(user.userStats?.timeline ?: userService.createDefaultUserStatsTimeline())
                        )
                    },
                listOf(
                    "userStats.trophyPoints,Trophies",
                    "userStats.participations,Participations",
                    "username,Username",
                    "country.name,Country"
                )
            )
        )
    }

    @RequestMapping("/{id}", method = [RequestMethod.POST])
    @Secured(AuthorityRole.ROLE_USER)
    fun changeUser(
        @RequestBody userChangeDto: UserChangeDto,
        @PathVariable("id") id: Long,
        request: HttpServletRequest
    ): ResponseEntity<JwtAuthenticationResponse>? {
        var user = assertUser(id)
        val upcomingUsernameChange = userChangeDto.username != user.username

        if (authService.authUser(request)!!.id != user.id) {
            throw RestException("You are not allowed to change another user.", HttpStatus.BAD_REQUEST, null)
        }

        try {
            user = userService.changeUser(
                user, userChangeDto.about, userChangeDto.username,
                if (userChangeDto.country != null) userService.findCountryById(userChangeDto.country).orElse(null) else null,
                userChangeDto.email
            )

            if (upcomingUsernameChange) {
                val userDetails = userDetailsService.loadUserByUsername(user.username)
                val token = jwtTokenUtil.generateToken(userDetails)

                return ResponseEntity.ok<JwtAuthenticationResponse>(JwtAuthenticationResponse(token))
            }
        } catch (e: UserService.InvalidUsernameException) {
            throw RestException("Username invalid.", HttpStatus.BAD_REQUEST, null)
        } catch (e: UserService.UsernameTakenException) {
            throw RestException("Username already taken.", HttpStatus.BAD_REQUEST, null)
        } catch (e: UserService.InvalidEmailException) {
            throw RestException("Email address is invalid.", HttpStatus.BAD_REQUEST, null)
        } catch (e: UserService.EmailExistsException) {
            throw RestException("Email address is already in use.", HttpStatus.BAD_REQUEST, null)
        }

        return null
    }

    @RequestMapping("/{id}/change-password", method = [RequestMethod.POST])
    @Secured(AuthorityRole.ROLE_USER)
    fun changePassword(
        @RequestBody passwordChangeDto: PasswordChangeDto,
        @PathVariable("id") id: Long,
        request: HttpServletRequest
    ) {
        if (authService.authUser(request)!!.id != assertUser(id).id) {
            throw RestException("You are not allowed to change another user.", HttpStatus.BAD_REQUEST, null)
        }
        try {
            userService.changePassword(assertUser(id), passwordChangeDto.currentPassword, passwordChangeDto.newPassword)
        } catch (e: CredentialException) {
            throw RestException("Wrong password.", HttpStatus.BAD_REQUEST, e)
        }
    }

    @RequestMapping("{id}/photo", method = [RequestMethod.POST], consumes = ["multipart/form-data"])
    @Secured(AuthorityRole.ROLE_USER)
    fun changePhoto(@RequestParam("photo") photo: MultipartFile, @PathVariable("id") userId: Long, request: HttpServletRequest) {
        val user = assertUser(userId)

        if (authService.authUser(request)!!.id != user.id) {
            throw RestException("Forbidden.", HttpStatus.FORBIDDEN, null)
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

    @RequestMapping("/{userId}/photo", method = [RequestMethod.DELETE])
    @Secured(AuthorityRole.ROLE_USER)
    fun deleteUserPhoto(@PathVariable("userId") userId: Long, request: HttpServletRequest) {
        val user = assertUser(userId)

        if (authService.getUserFromToken(request.getHeader(authService.tokenHeaderName))!!.id != user.id) {
            throw RestException("Forbidden.", HttpStatus.FORBIDDEN, null)
        }

        if (user.photo == null) return

        user.photo = null
        userService.saveUser(user)
    }

    @RequestMapping("/{id}/tetris", method = [RequestMethod.POST])
    @Secured(AuthorityRole.ROLE_USER)
    fun saveTetris(@PathVariable("id") userId: Long, @RequestBody highscore: Long, request: HttpServletRequest): ResponseEntity<TetrisDto> {
        if (authService.getUserFromToken(request.getHeader(authService.tokenHeaderName))!!.id != userId) {
            throw RestException("Forbidden", HttpStatus.FORBIDDEN, null)
        }

        val user = userService.getById(userId).orElseThrow { RestException("User $userId not found.", HttpStatus.NOT_FOUND, null) }
        return ResponseEntity.ok(TetrisDto.toDto(tetrisService.add(user, highscore, null)))
    }

    @RequestMapping("/password-forgotten", method = [RequestMethod.POST])
    fun passwordForgotten(@RequestBody body: Map<String, String>) {
        val user = body["email"]?.let { userService.findByEmail(it) }
            ?: throw RestException("Bad request", HttpStatus.BAD_REQUEST, null)
        userService.initiatePasswordReset(user)
    }

    @RequestMapping("/reset-password", method = [RequestMethod.POST])
    fun resetPassword(@RequestBody dto: PasswordResetDto): ResponseEntity<JwtAuthenticationResponse>? {
        val user = userService.findByResetKey(dto.resetKey)
            ?: throw RestException("Bad request", HttpStatus.BAD_REQUEST, null)
        try {
            userService.executePasswordReset(user, dto.resetKey, dto.password)

            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(user.username, dto.password)
            )

            securityContextHolderFacade.authentication = authentication

            val userDetails = userDetailsService.loadUserByUsername(user.username)
            val token = jwtTokenUtil.generateToken(userDetails)

            return ResponseEntity.ok(JwtAuthenticationResponse(token))
        } catch (e: IllegalArgumentException) {
            throw RestException("This didn't work out.", HttpStatus.BAD_REQUEST, e)
        } catch (e: IllegalStateException) {
            throw RestException("Your reset key has expired.", HttpStatus.BAD_REQUEST, e)
        }
    }

    private fun assertUser(id: Long): User = userService.getById(id)
        .orElseThrow { RestException("User $id not found", HttpStatus.NOT_FOUND, null) }
}
