package com.cwtsite.cwt.twitch.view.mapper

import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.twitch.view.model.TwitchUserDto
import org.springframework.stereotype.Component

@Component
class TwitchUserMapper {

    fun fromDto(dto: TwitchUserDto, user: User, title: String) = Channel(
        id = dto.id!!,
        user = user,
        title = title,
        displayName = dto.displayName,
        type = dto.type,
        profileImageUrl = dto.profileImageUrl,
        viewCount = dto.viewCount,
        broadcasterType = dto.broadcasterType,
        offlineImageUrl = dto.offlineImageUrl,
        login = dto.login,
        description = dto.description
    )
}
