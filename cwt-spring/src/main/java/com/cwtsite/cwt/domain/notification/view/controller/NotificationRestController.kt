package com.cwtsite.cwt.domain.notification.view.controller

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.domain.notification.entity.Notification
import com.cwtsite.cwt.domain.notification.service.NotificationService
import com.cwtsite.cwt.domain.notification.view.model.NotificationTypeDto
import com.cwtsite.cwt.domain.notification.view.model.NotificationViewDto
import com.cwtsite.cwt.domain.notification.view.model.NotificationWriteDto
import com.cwtsite.cwt.domain.user.repository.entity.AuthorityRole
import com.cwtsite.cwt.domain.user.service.AuthService
import com.cwtsite.cwt.domain.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("api/user/{userId}/notification")
class NotificationRestController @Autowired
constructor(
    private val userService: UserService,
    private val authService: AuthService,
    private val notificationService: NotificationService,
) {
    @GetMapping
    @Secured(AuthorityRole.ROLE_USER)
    fun fetch(
        @PathVariable("userId") id: Long,
        @RequestParam("sub", required = false) sub: String?,
        request: HttpServletRequest
    ): ResponseEntity<NotificationViewDto> {
        val user = userService.getById(id).orElseThrow { RestException("", HttpStatus.NOT_FOUND, null) }
        if (authService.authUser(request)!!.id != user.id) throw RestException("", HttpStatus.FORBIDDEN, null)
        return when (sub) {
            null -> NotificationViewDto.empty(user)
            else -> {
                when (val n = notificationService.findSubscriptionForUser(user, sub)) {
                    null -> NotificationViewDto.empty(user)
                    else -> NotificationViewDto.toDto(n)
                }
            }
        }.let { ResponseEntity.ok(it) }
    }

    @PostMapping
    @Secured(AuthorityRole.ROLE_USER)
    fun save(
        @PathVariable("userId") id: Long,
        @RequestBody dto: NotificationWriteDto,
        request: HttpServletRequest
    ): ResponseEntity<NotificationViewDto> {
        val user = userService.getById(id).orElseThrow { RestException("", HttpStatus.NOT_FOUND, null) }
        if (authService.authUser(request)!!.id != user.id) throw RestException("", HttpStatus.FORBIDDEN, null)
        val sub = dto.subscription["endpoint"].asText()
        val userAgent = request.getHeader("User-Agent")
        val n = notificationService.findSubscriptionForUser(user, sub) ?: Notification(
            user = user,
            subscription = dto.subscription.toString(),
            subscriptionCreated = Instant.now(),
            setting = dto.setting?.let { NotificationTypeDto.fromDtos(it) } ?: 0,
            userAgent = userAgent,
        )
        dto.setting?.let { n.setting = NotificationTypeDto.fromDtos(it) }
        n.userAgent = userAgent
        return ResponseEntity
            .status(if (n.id == null) HttpStatus.CREATED else HttpStatus.OK)
            .body(NotificationViewDto.toDto(notificationService.save(n)))
    }
}