package com.cwtsite.cwt.domain.stream.view.mapper

import com.cwtsite.cwt.domain.game.view.mapper.GameDetailMapper
import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.stream.entity.Stream
import com.cwtsite.cwt.domain.stream.view.model.StreamDto
import com.cwtsite.cwt.twitch.model.TwitchVideoDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class StreamMapper {

    @Autowired private lateinit var channelMapper: ChannelMapper
    @Autowired private lateinit var gameDetailMapper: GameDetailMapper

    fun toDto(stream: Stream) = StreamDto(
        id = stream.id,
        channel = channelMapper.toDto(stream.channel),
        userId = stream.userId,
        userName = stream.userName,
        title = stream.title,
        description = stream.description,
        createdAt = stream.createdAt,
        publishedAt = stream.publishedAt,
        url = stream.url,
        thumbnailUrl = stream.thumbnailUrl,
        viewable = stream.viewable,
        viewCount = stream.viewCount,
        language = stream.language,
        type = stream.type,
        duration = stream.duration,
        game = stream.game?.let { gameDetailMapper.toDto(it) }
    )

    fun toDto(dto: TwitchVideoDto, channel: Channel) = StreamDto(
        id = dto.id,
        channel = channelMapper.toDto(channel),
        userId = dto.userId,
        userName = dto.userName,
        title = dto.title,
        description = dto.description,
        createdAt = dto.createdAt,
        publishedAt = dto.publishedAt,
        url = dto.url,
        thumbnailUrl = dto.thumbnailUrl,
        viewable = dto.viewable,
        viewCount = dto.viewCount,
        language = dto.language,
        type = dto.type,
        duration = dto.duration,
        game = null
    )

    fun fromDto(dto: StreamDto, channel: Channel) = Stream(
        id = dto.id,
        channel = channel,
        userId = dto.userId,
        userName = dto.userName,
        title = dto.title,
        description = dto.description,
        createdAt = dto.createdAt,
        publishedAt = dto.publishedAt,
        url = dto.url,
        thumbnailUrl = dto.thumbnailUrl,
        viewable = dto.viewable,
        viewCount = dto.viewCount,
        language = dto.language,
        type = dto.type,
        duration = dto.duration,
        game = null
    )
}
