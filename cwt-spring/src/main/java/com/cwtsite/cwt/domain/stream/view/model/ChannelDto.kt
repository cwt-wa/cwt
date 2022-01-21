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
) {

    companion object {
        fun toDto(channel: Channel) = ChannelDto(
            id = channel.id,
            title = channel.title,
            user = UserMinimalDto.toDto(channel.user),
            displayName = channel.displayName,
            type = channel.type,
            profileImageUrl = channel.profileImageUrl,
            viewCount = channel.viewCount,
            broadcasterType = channel.broadcasterType,
            offlineImageUrl = channel.offlineImageUrl,
            login = channel.login,
            botAutoJoin = channel.botAutoJoin,
            description = channel.description,
            created = channel.created
        )
    }
}
