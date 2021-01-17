package com.cwtsite.cwt.domain.stream.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import java.time.Instant

@DataTransferObject
data class ChannelDto(
        val id: String? = null,
        val title: String? = null,
        val user: UserMinimalDto,
        val displayName: String? = null,
        val type: String? = null,
        val profileImageUrl: String? = null,
        val viewCount: Long? = null,
        val broadcasterType: String? = null,
        val offlineImageUrl: String? = null,
        val login: String? = null,
        val description: String? = null,
        val botAutoJoin: Boolean,
        val modified: Instant? = null,
        val created: Instant? = null
) 

