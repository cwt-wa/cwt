package com.cwtsite.cwt.domain.message.view.controller

import com.cwtsite.cwt.domain.core.view.model.PageDto
import com.cwtsite.cwt.domain.message.service.MessageService
import com.cwtsite.cwt.domain.message.view.model.MessageCreationDto
import com.cwtsite.cwt.domain.message.view.model.MessageDto
import com.cwtsite.cwt.domain.user.repository.entity.AuthorityRole
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.AuthService
import com.cwtsite.cwt.domain.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import java.sql.Timestamp
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("api/message")
class MessageRestController {

    @Autowired private lateinit var messageService: MessageService
    @Autowired private lateinit var authService: AuthService
    @Autowired private lateinit var userService: UserService

    @RequestMapping("", method = [RequestMethod.GET])
    fun getMessages(pageDto: PageDto<MessageDto>, request: HttpServletRequest): ResponseEntity<PageDto<MessageDto>> {
        val authorizationHeader = request.getHeader(authService.tokenHeaderName)
        var authenticatedUser: User? = null
        if (authorizationHeader != null) authenticatedUser = authService.getUserFromToken(authorizationHeader)

        val messages = when (authenticatedUser) {
            null -> messageService.findMessagesForGuest(pageDto.start, pageDto.size)
            else -> messageService.findMessagesForUser(authenticatedUser, pageDto.start, pageDto.size)
        }

        return ResponseEntity.ok(PageDto.toDto(messages.map { MessageDto.toDto(it) }, listOf<String>()))
    }

    @RequestMapping("/new", method = [RequestMethod.GET])
    fun getNewMessages(after: Long, request: HttpServletRequest): ResponseEntity<List<MessageDto>> {
        val authorizationHeader = request.getHeader(authService.tokenHeaderName)
        var authenticatedUser: User? = null
        if (authorizationHeader != null) authenticatedUser = authService.getUserFromToken(authorizationHeader)
        val created = Timestamp(after)

        val messages = when (authenticatedUser) {
            null -> messageService.findNewMessagesForGuest(created)
            else -> messageService.findNewMessagesForUser(authenticatedUser, created)
        }

        return ResponseEntity.ok(messages.map { MessageDto.toDto(it) })
    }

    @RequestMapping("/admin", method = [RequestMethod.GET])
    @Secured(AuthorityRole.ROLE_ADMIN)
    fun getMessagesForAdmin(pageDto: PageDto<MessageDto>): ResponseEntity<PageDto<MessageDto>> {
        val messages = messageService.findMessagesForAdmin(pageDto.start, pageDto.size)
        return ResponseEntity.ok(PageDto.toDto(messages.map { MessageDto.toDto(it) }, listOf<String>()))
    }

    @RequestMapping("/admin/new", method = [RequestMethod.GET])
    @Secured(AuthorityRole.ROLE_ADMIN)
    fun getNewMessagesForAdmin(after: Long): ResponseEntity<List<MessageDto>> =
            ResponseEntity.ok(messageService.findNewMessagesForAdmin(Timestamp(after)).map { MessageDto.toDto(it) })

    @RequestMapping("/{id}", method = [RequestMethod.DELETE])
    @Secured(AuthorityRole.ROLE_ADMIN)
    fun deleteMessage(@PathVariable("id") id: Long) = messageService.deleteMessage(id)

    @RequestMapping("", method = [RequestMethod.POST])
    @Secured(AuthorityRole.ROLE_USER)
    fun addMessage(@RequestBody dto: MessageCreationDto, request: HttpServletRequest): ResponseEntity<MessageDto> {
        val authenticatedUser = authService.getUserFromToken(request.getHeader(authService.tokenHeaderName))
        val savedMessage = messageService.save(MessageCreationDto.fromDto(
                dto, authenticatedUser!!, userService.getByIds(dto.recipients!!)))
        return ResponseEntity.ok(MessageDto.toDto(savedMessage))
    }
}

