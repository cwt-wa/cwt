package com.cwtsite.cwt.twitch

import com.cwtsite.cwt.twitch.model.TwitchStreamDto
import com.cwtsite.cwt.twitch.model.TwitchVideoDto
import java.time.LocalDateTime

interface TwitchService {

    var lastVideosRequest: LocalDateTime?
    var lastStreamsRequest: LocalDateTime?

    /**
     * @param channelIds In Twitch speech these are the user IDs.
     */
    fun requestVideos(channelIds: List<String>): List<TwitchVideoDto>

    fun requestStreams(): List<TwitchStreamDto>
}
