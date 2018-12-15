package com.cwtsite.cwt.domain.user.view.controller

import com.cwtsite.cwt.domain.application.service.ApplicationService
import com.cwtsite.cwt.domain.core.exception.NotFoundException
import com.cwtsite.cwt.domain.core.view.model.PageDto
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.UserService
import com.cwtsite.cwt.domain.user.view.model.UserDetailDto
import com.cwtsite.cwt.domain.user.view.model.UserOverviewDto
import com.cwtsite.cwt.domain.user.view.model.UserStatsDto
import com.cwtsite.cwt.entity.Application
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("api/user")
class UserRestController @Autowired
constructor(private val userService: UserService, private val applicationService: ApplicationService) {

    @RequestMapping("", method = [RequestMethod.GET])
    fun register(): ResponseEntity<List<User>> {
        return ResponseEntity.ok(userService.findAllOrderedByUsername())
    }

    @RequestMapping("/{id}/can-apply", method = [RequestMethod.GET])
    fun userCanApplyForTournament(@PathVariable("id") id: Long): ResponseEntity<Boolean> {
        return ResponseEntity.ok(this.userService.userCanApplyForCurrentTournament(assertUser(id)))
    }

    @RequestMapping("/{usernameOrId}", method = [RequestMethod.GET])
    fun getOne(@PathVariable("usernameOrId") usernameOrId: Any): ResponseEntity<UserDetailDto> {
        return if (usernameOrId is String) {
            ResponseEntity.ok(UserDetailDto.toDto(userService.findByUsername(usernameOrId)))
        } else {
            ResponseEntity.ok(UserDetailDto.toDto(assertUser(usernameOrId as Long)))
        }
    }

    @RequestMapping("/{id}/can-report", method = [RequestMethod.GET])
    fun userCanReportForCurrentTournament(@PathVariable("id") id: Long): ResponseEntity<Boolean> {
        return ResponseEntity.ok(this.userService.userCanReportForCurrentTournament(assertUser(id)))
    }

    @RequestMapping("/{id}/application", method = [RequestMethod.POST])
    fun applyForTournament(@PathVariable("id") id: Long): ResponseEntity<Application> {
        return ResponseEntity.ok(this.applicationService.apply(assertUser(id)))
    }

    @RequestMapping("/{id}/group/remaining-opponents", method = [RequestMethod.GET])
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
                        .map { user -> UserOverviewDto.toDto(user, UserStatsDto.toDtos(user.userStats?.timeline)) },
                Arrays.asList(
                        "userStats.trophyPoints,Trophies",
                        "userStats.participations,Participations",
                        "username,Username",
                        "userProfile.country,Country")))
    }

    private fun assertUser(id: Long): User = userService.getById(id).orElseThrow { NotFoundException() }
}
