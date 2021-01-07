package com.cwtsite.cwt.domain.message.service

import com.cwtsite.cwt.domain.message.entity.Message
import com.cwtsite.cwt.domain.message.entity.enumeration.MessageCategory
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.time.Instant
import kotlin.math.min

@Component
class MessageService @Autowired
constructor(private val messageRepository: MessageRepository,
            private val userService: UserService,
            private val messageEventListener: MessageEventListener) {

    fun findNewMessages(after: Instant, size: Int, user: User?,
                        categories: List<MessageCategory> = MessageCategory.values().toList()): List<Message> {
        // offset by one because DB might store with greater precision
        val result = messageRepository.findNewByAuthorOrRecipientsInOrCategoryInOrderByCreatedDesc(user, categories, after.plusMillis(1))
        return result.subList(0, min(size, result.size))
    }

    fun findOldMessages(before: Instant, size: Int, user: User?,
                        categories: List<MessageCategory> = MessageCategory.values().toList()): List<Message> {
        val result = messageRepository.findOldByAuthorOrRecipientsInOrCategoryInOrderByCreatedDesc(user, categories, before)
        return result.subList(0, min(size, result.size))
    }

    @Transactional
    fun save(message: Message): Message {
        val persisted = messageRepository.save(message)
        TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
            override fun afterCommit() {
                messageEventListener.publish(persisted)
            }
        })
        return persisted
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun publishNews(type: MessageNewsType, author: User, vararg data: Any?): Message {
        val body = when (type) {
            MessageNewsType.STREAM -> "${data[0]},${data.drop(1).joinToString("")}"
            MessageNewsType.TWITCH_MESSAGE, MessageNewsType.DISCORD_MESSAGE ->
                "${data[0]},${data[1]},${data.drop(2).joinToString("")}"
            else -> data.joinToString(separator = ",")
        }
        val persisted = messageRepository.save(Message(
                category = MessageCategory.NEWS,
                author = author, newsType = type, body = body))
        TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
            override fun afterCommit() {
                messageEventListener.publish(persisted)
            }
        })
        return persisted
    }

    fun deleteMessage(id: Long) {
        messageRepository.deleteById(id)
    }

    fun findMessagesForAdminCreatedBefore(before: Instant, size: Int): List<Message> {
        val result = messageRepository.findAllByCreatedBeforeOrderByCreatedDesc(before)
        return result.subList(0, min(size, result.size))
    }

    fun findMessagesForAdminCreatedAfter(after: Instant, size: Int): List<Message> {
        // offset by one because DB might store with greater precision
        val result = messageRepository.findAllByCreatedAfterOrderByCreatedDesc(after.plusMillis(1))
        return result.subList(0, min(size, result.size))
    }

    @Transactional
    fun thirdPartyMessage(displayName: String, link: String, body: String,
                          newsType: MessageNewsType): Message =
            publishNews(
                    newsType,
                    userService.getById(1).orElseThrow(),
                    displayName, link, body)
}
