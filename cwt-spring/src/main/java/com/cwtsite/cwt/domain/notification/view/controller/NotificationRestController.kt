package com.cwtsite.cwt.domain.notification.view.controller

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.domain.notification.entity.Notification
import com.cwtsite.cwt.domain.notification.service.NotificationService
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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
        request: HttpServletRequest
    ): ResponseEntity<NotificationViewDto> {
        val user = userService.getById(id)
            .orElseThrow { RestException("", HttpStatus.NOT_FOUND, null) }
        if (authService.authUser(request)!!.id != user.id) {
            throw RestException("", HttpStatus.FORBIDDEN, null)
        }
        return ResponseEntity.ok(
            notificationService.findForUser(user)?.let { NotificationViewDto.toDto(it) }
                ?: NotificationViewDto.empty(user)
        )
    }

    @PostMapping
    @Secured(AuthorityRole.ROLE_USER)
    fun save(
        @PathVariable("userId") id: Long,
        dto: NotificationWriteDto,
        request: HttpServletRequest
    ): ResponseEntity<NotificationViewDto> {
        val user = userService.getById(id)
            .orElseThrow { RestException("", HttpStatus.NOT_FOUND, null) }
        if (authService.authUser(request)!!.id != user.id) {
            throw RestException("", HttpStatus.FORBIDDEN, null)
        }
        val notification = notificationService.findForUser(user)?.let {
            if (dto.setting != null) it.setting = dto.setting
            if (dto.subscription != null) it.subscription = dto.subscription
            if (dto.userAgent != null) it.userAgent = dto.userAgent
            notificationService.save(it)
        } ?: run {
            if (dto.setting == null || dto.subscription == null) {
                throw RestException("Setting and subscription are required.", HttpStatus.BAD_REQUEST, null)
            }
            notificationService.save(
                Notification(
                    userAgent = dto.userAgent,
                    subscription = dto.subscription,
                    setting = dto.setting,
                    user = user
                )
            )
        }
        return ResponseEntity.ok(NotificationViewDto.toDto(notification))
    }
}