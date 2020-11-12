package com.cwtsite.cwt.domain.stream.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.game.view.model.GameDetailDto
import com.cwtsite.cwt.domain.stream.entity.Stream

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
) {

    companion object {

        fun toDto(stream: Stream, playoffRoundLocalized: String?) = StreamDto(
                id = stream.id,
                channel = ChannelDto.toDto(stream.channel),
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
                game = stream.game?.let { GameDetailDto.toDto(it, playoffRoundLocalized) }
        )
    }
}
