package com.cwtsite.cwt.domain.message.view.model

import com.cwtsite.cwt.domain.message.entity.Message
import com.cwtsite.cwt.domain.message.entity.enumeration.MessageCategory
import com.cwtsite.cwt.domain.user.repository.entity.User

data class MessageDto(

        var body: String,
        var category: MessageCategory,
        var recipients: List<Long>? = null
) {

    companion object {

        fun map(dto: MessageDto, author: User, recipients: List<User>) = Message(
                author = author,
                body = dto.body,
                category = dto.category,
                recipients = recipients.toMutableList()
        )
    }

}
