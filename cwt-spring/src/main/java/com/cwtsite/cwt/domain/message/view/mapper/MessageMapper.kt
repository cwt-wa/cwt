package com.cwtsite.cwt.domain.message.view.mapper

import com.cwtsite.cwt.domain.message.entity.Message
import com.cwtsite.cwt.domain.message.view.model.MessageDto
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import org.springframework.stereotype.Component

@Component
class MessageMapper {

    fun toDto(message: Message): MessageDto = MessageDto(
        id = message.id!!,
        created = message.created!!,
        body = message.body,
        recipients = message.recipients.map { UserMinimalDto(id = it.id!!, username = it.username) },
        author = UserMinimalDto(id = message.author.id!!, username = message.author.username),
        newsType = message.newsType,
        category = message.category
    )
}
