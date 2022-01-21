package com.cwtsite.cwt.domain.message.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.message.entity.Message
import com.cwtsite.cwt.domain.message.entity.enumeration.MessageCategory
import com.cwtsite.cwt.domain.message.service.MessageNewsType
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import java.time.Instant

@DataTransferObject
data class MessageDto(
    val id: Long,
    val created: Instant,
    val body: String,
    val recipients: List<UserMinimalDto>,
    val author: UserMinimalDto,
    val newsType: MessageNewsType?,
    val category: MessageCategory
) {

    companion object {

        fun toDto(message: Message): MessageDto = MessageDto(
            id = message.id!!,
            created = message.created!!,
            body = message.body,
            recipients = message.recipients.map { UserMinimalDto.toDto(it) },
            author = UserMinimalDto.toDto(message.author),
            newsType = message.newsType,
            category = message.category
        )
    }
}
