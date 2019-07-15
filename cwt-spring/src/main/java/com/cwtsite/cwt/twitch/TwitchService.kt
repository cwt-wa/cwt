package com.cwtsite.cwt.twitch

import com.cwtsite.cwt.twitch.model.TwitchStreamDto
import com.cwtsite.cwt.twitch.model.TwitchVideoDto

interface TwitchService {

    /**
     * @param channelIds In Twitch speech these are the user IDs.
     */
    fun requestVideos(channelIds: List<String>): List<TwitchVideoDto>

    fun requestStreams(): List<TwitchStreamDto>
}
