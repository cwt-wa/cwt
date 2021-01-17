package com.cwtsite.cwt.domain.stream.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.game.view.model.GameDetailDto
import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.stream.entity.Stream
import com.cwtsite.cwt.twitch.model.TwitchVideoDto

@DataTransferObject
data class StreamDto(
        var id: String,
        var channel: ChannelDto,
        var userId: String?,
        var userName: String?,
        var title: String?,
        var description: String?,
        var createdAt: String?,
        var publishedAt: String?,
        var url: String?,
        var thumbnailUrl: String?,
        var viewable: String?,
        var viewCount: Long,
        var language: String?,
        var type: String?,
        var duration: String?,
        var game: GameDetailDto?
) 

