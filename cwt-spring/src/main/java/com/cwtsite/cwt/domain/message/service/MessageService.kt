package com.cwtsite.cwt.domain.message.service

import com.cwtsite.cwt.domain.message.entity.Message
import com.cwtsite.cwt.domain.message.entity.enumeration.MessageCategory
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class MessageService @Autowired
constructor(private val messageRepository: MessageRepository) {

    fun findMessagesForGuest(): List<Message> {
        return messageRepository.findTop100ByCategoryInOrderByCreatedDesc(MessageCategory.guestCategories())
    }

    fun findMessagesForUser(user: User): List<Message> {
        return messageRepository.findTop100ByAuthorOrRecipientsInOrCategoryInOrderByCreatedDesc(
                user, listOf(user), MessageCategory.guestCategories())
    }

    fun save(message: Message): Message {
        return messageRepository.save(message)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun publishNews(type: MessageNewsType, vararg data: Any?) =
            messageRepository.save(Message(category = MessageCategory.NEWS, newsType = type,body = data.joinToString(separator = ",")))
}
