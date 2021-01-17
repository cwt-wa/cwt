package com.cwtsite.cwt.domain.stream.view.mapper

import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.stream.view.model.ChannelDto
import com.cwtsite.cwt.domain.user.view.model.UserMinimalDto
import org.springframework.stereotype.Component

@Component
class ChannelMapper {

    fun toDto(channel: Channel) = ChannelDto(
        id = channel.id,
        title = channel.title,
        user = UserMinimalDto(id = channel.user.id!!, username = channel.user.username),
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

