package com.cwtsite.cwt.domain.message.view.controller

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.domain.message.entity.enumeration.MessageCategory
import com.cwtsite.cwt.domain.message.service.MessageService
import com.cwtsite.cwt.domain.message.view.model.MessageCreationDto
import com.cwtsite.cwt.domain.message.view.model.MessageDto
import com.cwtsite.cwt.domain.user.repository.entity.AuthorityRole
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.AuthService
import com.cwtsite.cwt.domain.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
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
    fun getMessages(@RequestParam("after", required = false) after: Long?,
                    @RequestParam("before", required = false) before: Long?,
                    @RequestParam("size", defaultValue = "30") size: Int,
                    @RequestParam("category", required = false) category: MessageCategory?,
                    request: HttpServletRequest): ResponseEntity<List<MessageDto>> {
        val authorizationHeader = request.getHeader(authService.tokenHeaderName)
        var user: User? = null
        if (authorizationHeader != null) user = authService.getUserFromToken(authorizationHeader)
        val messages = if (after == null && before != null) {
            messageService.findOldMessages(
                    Timestamp(before), size, user,
                    if (category == null) MessageCategory.values().toList() else listOf(category))
        } else if (after != null && before == null) {
            messageService.findNewMessages(
                    Timestamp(after), size, user,
                    if (category == null) MessageCategory.values().toList() else listOf(category))
        } else {
            throw RestException("Either after or before must be specified", HttpStatus.BAD_REQUEST, null)
        }
        return ResponseEntity.ok(messages.map { MessageDto.toDto(it) })
    }

    @RequestMapping("/admin", method = [RequestMethod.GET])
    @Secured(AuthorityRole.ROLE_ADMIN)
    fun getMessagesForAdmin(start: Int, size: Int): ResponseEntity<List<MessageDto>> {
        val messages = messageService.findAll(start, size)
        return ResponseEntity.ok(messages.content.map { MessageDto.toDto(it) })
    }

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

