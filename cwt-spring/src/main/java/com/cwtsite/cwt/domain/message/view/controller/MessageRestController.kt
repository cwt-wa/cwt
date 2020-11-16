package com.cwtsite.cwt.domain.message.view.controller

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.core.event.SseEmitterFactory
import com.cwtsite.cwt.domain.message.entity.enumeration.MessageCategory
import com.cwtsite.cwt.domain.message.service.MessageEventListener
import com.cwtsite.cwt.domain.message.service.MessageService
import com.cwtsite.cwt.domain.message.view.model.MessageCreationDto
import com.cwtsite.cwt.domain.message.view.model.MessageDto
import com.cwtsite.cwt.domain.message.view.model.TwitchMessageDto
import com.cwtsite.cwt.domain.user.repository.entity.AuthorityRole
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.AuthService
import com.cwtsite.cwt.domain.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.sql.Timestamp
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("api/message")
class MessageRestController {

    @Autowired private lateinit var messageService: MessageService
    @Autowired private lateinit var authService: AuthService
    @Autowired private lateinit var userService: UserService
    @Autowired private lateinit var sseEmitterFactory: SseEmitterFactory
    @Autowired private lateinit var messageEventListener: MessageEventListener

    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/listen", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun listen(request: HttpServletRequest): ResponseBodyEmitter {
        val emitter = sseEmitterFactory.createInstance()
        val authHeader = request.getHeader(authService.tokenHeaderName)
        val user = if (authHeader != null) authService.getUserFromToken(authHeader) else null
        val emit = { message: MessageDto ->
            if (message.category === MessageCategory.PRIVATE) {
                if (user != null && (message.author.id == user.id || message.recipients.map { it.id }.contains(user.id))) {
                    logger.info("Not publishing message to user as it's private.")
                    emitter.send(SseEmitter.event().data(message).name("EVENT"))
                }
            } else {
                logger.info("Publishing message to the user.")
                emitter.send(SseEmitter.event().data(message).name("EVENT"))
            }
        }
        messageEventListener.listen { message ->
            logger.info("listener received message $message")
            emit(MessageDto.toDto(message))
        }
        // we're always providing the latest few because there could've
        //  been a few misses during a reconnection
        logger.info("Sending the last few messages to the user initially.")
        getMessages(
                after = 0,
                size = 7,
                before = null,
                request = request,
                category = null).body!!.forEach { emit(it) }
        return emitter
    }

    @RequestMapping("", method = [RequestMethod.GET])
    fun getMessages(@RequestParam("after", required = false) after: Long?,
                    @RequestParam("before", required = false) before: Long?,
                    @RequestParam("size", defaultValue = "30") size: Int,
                    @RequestParam("category", required = false) category: MessageCategory?,
                    request: HttpServletRequest): ResponseEntity<List<MessageDto>> {
        val authorizationHeader = request.getHeader(authService.tokenHeaderName)
        var user: User? = null
        if (authorizationHeader != null) user = authService.getUserFromToken(authorizationHeader)
        val categories = if (category == null) {
            if (user == null) MessageCategory.guestCategories() else MessageCategory.values().toList()
        } else {
            if (user == null) listOf(category).filter { MessageCategory.guestCategories().contains(it) } else listOf(category)
        }
        val messages = if (after == null && before != null) {
            messageService.findOldMessages(
                    Timestamp(before), size, user,
                    categories)
        } else if (after != null && before == null) {
            messageService.findNewMessages(
                    Timestamp(after), size, user,
                    categories)
        } else {
            throw RestException("Either after or before must be specified", HttpStatus.BAD_REQUEST, null)
        }
        return ResponseEntity.ok(messages.map { MessageDto.toDto(it) })
    }

    @RequestMapping("/admin", method = [RequestMethod.GET])
    @Secured(AuthorityRole.ROLE_ADMIN)
    fun getMessagesForAdmin(@RequestParam("after", required = false) after: Long?,
                            @RequestParam("before", required = false) before: Long?,
                            @RequestParam("size", defaultValue = "30") size: Int): ResponseEntity<List<MessageDto>> {
        val messages = if (after == null && before != null) {
            messageService.findMessagesForAdminCreatedBefore(Timestamp(before), size)
        } else if (after != null && before == null) {
            messageService.findMessagesForAdminCreatedAfter(Timestamp(after), size)
        } else {
            throw RestException("Either after or before must be specified", HttpStatus.BAD_REQUEST, null)
        }
        return ResponseEntity.ok(messages.map { MessageDto.toDto(it) })
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

    @PostMapping("twitch")
    fun createMessageFromTwitch(@RequestBody message: TwitchMessageDto): ResponseEntity<MessageDto> {
        val message = messageService.createTwitchMessage(message.displayName, message.channelName, message.body)
        return ResponseEntity.ok(MessageDto.toDto(message))
    }
}

