package com.cwtsite.cwt.domain.message.view.controller

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.core.event.SseEmitterFactory
import com.cwtsite.cwt.domain.message.entity.Message
import com.cwtsite.cwt.domain.message.entity.enumeration.MessageCategory
import com.cwtsite.cwt.domain.message.service.MessageEventListener
import com.cwtsite.cwt.domain.message.service.MessageNewsType
import com.cwtsite.cwt.domain.message.service.MessageService
import com.cwtsite.cwt.domain.message.view.model.MessageCreationDto
import com.cwtsite.cwt.domain.message.view.model.MessageDto
import com.cwtsite.cwt.domain.message.view.model.ThirdPartyMessageDto
import com.cwtsite.cwt.domain.user.repository.entity.AuthorityRole
import com.cwtsite.cwt.domain.user.service.AuthService
import com.cwtsite.cwt.domain.user.service.UserService
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter
import java.time.Instant
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("api/message")
class MessageRestController {

    @Autowired private lateinit var messageService: MessageService
    @Autowired private lateinit var authService: AuthService
    @Autowired private lateinit var userService: UserService
    @Autowired private lateinit var sseEmitterFactory: SseEmitterFactory
    @Autowired private lateinit var messageEventListener: MessageEventListener

    @Value("\${cwt.third-party-token}") private lateinit var thirdPartyToken: String

    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/listen", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun listen(request: HttpServletRequest): ResponseBodyEmitter {
        val emitter = sseEmitterFactory.createInstance()
        val listener = { message: Message ->
            logger.info("listener received message $message")
            if (message.category === MessageCategory.PRIVATE) {
                logger.info("Publishing existence of private message.")
                emitter.send("PRIVATE", "MESSAGE")
            } else {
                logger.info("Publishing message to the user.")
                emitter.send("EVENT", message)
            }
        }
        messageEventListener.listen(listener)
        emitter.onCompletion { messageEventListener.deafen(listener) }
        return emitter.delegate
    }

    @RequestMapping("", method = [RequestMethod.GET])
    fun getMessages(
        @RequestParam("after", required = false) after: Long?,
        @RequestParam("before", required = false) before: Long?,
        @RequestParam("size", defaultValue = "30") size: Int,
        @RequestParam("category", required = false) category: MessageCategory?,
        request: HttpServletRequest
    ): ResponseEntity<List<MessageDto>> {
        val user = authService.authUser(request)
        val categories = if (category == null) {
            if (user == null) MessageCategory.guestCategories() else MessageCategory.values().toList()
        } else {
            if (user == null) listOf(category).filter { MessageCategory.guestCategories().contains(it) } else listOf(category)
        }
        val messages = if (after == null && before != null) {
            messageService.findOldMessages(Instant.ofEpochMilli(before), size, user, categories)
        } else if (after != null && before == null) {
            messageService.findNewMessages(Instant.ofEpochMilli(after), size, user, categories)
        } else {
            throw RestException("Either after or before must be specified", HttpStatus.BAD_REQUEST, null)
        }
        return ResponseEntity.ok(messages.map { MessageDto.toDto(it) })
    }

    @RequestMapping("/admin", method = [RequestMethod.GET])
    @Secured(AuthorityRole.ROLE_ADMIN)
    fun getMessagesForAdmin(
        @RequestParam("after", required = false) after: Long?,
        @RequestParam("before", required = false) before: Long?,
        @RequestParam("size", defaultValue = "30") size: Int
    ): ResponseEntity<List<MessageDto>> {
        val messages = if (after == null && before != null) {
            messageService.findMessagesForAdminCreatedBefore(Instant.ofEpochMilli(before), size)
        } else if (after != null && before == null) {
            messageService.findMessagesForAdminCreatedAfter(Instant.ofEpochMilli(after), size)
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
        val user = authService.authUser(request)
        val savedMessage = messageService.save(
            MessageCreationDto.fromDto(
                dto, user!!, userService.getByIds(dto.recipients!!)
            )
        )
        return ResponseEntity.ok(MessageDto.toDto(savedMessage))
    }

    @GetMapping("/suggestions")
    @Secured(AuthorityRole.ROLE_USER)
    fun findSuggestions(
        @RequestParam("q", required = false) q: String?,
        request: HttpServletRequest
    ): ResponseEntity<List<UserMinimalDto>> {
        val res = if (q != null && q.isNotEmpty()) {
            userService.findByUsernameStartsWithIgnoreCase(q)
        } else {
            val user = authService.authUser(request)
            messageService.genSuggestions(user!!)
        }
        return ResponseEntity.ok(res.map { UserMinimalDto.toDto(it) })
    }

    @PostMapping("third-party")
    fun createMessageFromTwitch(
        @RequestBody dto: ThirdPartyMessageDto,
        @RequestHeader("third-party-token") thirdPartyTokenHeader: String,
        request: HttpServletRequest
    ): ResponseEntity<MessageDto> {
        if (thirdPartyTokenHeader != thirdPartyToken) throw RestException("Forbidden", HttpStatus.FORBIDDEN, null)
        if (!MessageNewsType.thirdPartyMessageTypes().contains(dto.newsType)) {
            throw RestException("Not a third party news type", HttpStatus.BAD_REQUEST, null)
        }
        val message = messageService.thirdPartyMessage(dto.displayName, dto.link, dto.body, dto.newsType)
        return ResponseEntity.ok(MessageDto.toDto(message))
    }
}
