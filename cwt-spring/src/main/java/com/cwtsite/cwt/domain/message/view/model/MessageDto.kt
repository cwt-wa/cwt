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
) 

