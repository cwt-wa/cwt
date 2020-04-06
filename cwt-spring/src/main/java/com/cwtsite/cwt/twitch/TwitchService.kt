package com.cwtsite.cwt.twitch

import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.twitch.model.TwitchStreamDto
import com.cwtsite.cwt.twitch.model.TwitchUserDto
import com.cwtsite.cwt.twitch.model.TwitchVideoDto
import java.time.LocalDateTime

interface TwitchService {

    var lastVideosRequest: LocalDateTime?
    var lastStreamsRequest: LocalDateTime?

    /**
     * @param channelIds In Twitch speech these are the user IDs.
     */
    fun requestVideos(channels: List<Channel>): List<TwitchVideoDto>

    fun requestStreams(channelIds: List<String>): List<TwitchStreamDto>

    fun requestUsers(vararg loginNames: String): List<TwitchUserDto>
}
