package com.cwtsite.cwt.domain.message.service

import com.cwtsite.cwt.domain.message.entity.Message
import com.cwtsite.cwt.domain.message.entity.enumeration.MessageCategory
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp

@Component
class MessageService @Autowired
constructor(private val messageRepository: MessageRepository) {

    fun findMessagesForGuest(start: Int, size: Int): Page<Message> {
        return messageRepository.findAllByCategoryInOrderByCreatedDesc(
                PageRequest.of(start, size, Sort.by(Sort.Direction.DESC, "created")),
                MessageCategory.guestCategories())
    }

    fun findNewMessagesForGuest(created: Timestamp): List<Message> =
            messageRepository.findAllByCategoryInAndCreatedAfterOrderByCreatedDesc(
                    MessageCategory.guestCategories(), created)

    fun findMessagesForUser(user: User, start: Int, size: Int): Page<Message> {
        return messageRepository.findAllByAuthorOrRecipientsInOrCategoryInOrderByCreatedDesc(
                PageRequest.of(start, size, Sort.by(Sort.Direction.DESC, "created")),
                user, listOf(user), MessageCategory.guestCategories());
    }

    fun findNewMessagesForUser(user: User, created: Timestamp): List<Message> =
            messageRepository.findNewByAuthorOrRecipientsInOrCategoryInOrderByCreatedDesc(
                    user, MessageCategory.guestCategories(), created)

    fun save(message: Message): Message {
        return messageRepository.save(message)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun publishNews(type: MessageNewsType, author: User, vararg data: Any?) =
            messageRepository.save(Message(category = MessageCategory.NEWS, author = author, newsType = type, body = data.joinToString(separator = ",")))

    fun findMessagesForAdmin(start: Int, size: Int) =
            messageRepository.findAll(PageRequest.of(start, size, Sort.by(Sort.Direction.DESC, "created")))

    fun findNewMessagesForAdmin(after: Timestamp): List<Message> = messageRepository.findAllByCreatedAfter(after)

    fun deleteMessage(id: Long) {
        messageRepository.deleteById(id)
    }
}
