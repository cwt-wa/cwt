package com.cwtsite.cwt.twitch.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.fasterxml.jackson.annotation.JsonProperty

@DataTransferObject
data class TwitchStreamDto(
    @JsonProperty("id") var id: String?,
    @JsonProperty("user_id") var userId: String?,
    @JsonProperty("user_name") var userName: String?,
    @JsonProperty("game_id") var gameId: String?,
    @JsonProperty("community_ids") var communityIds: List<String>?,
    @JsonProperty("type") var type: String?,
    @JsonProperty("title") var title: String?,
    @JsonProperty("viewer_count") var viewerCount: Long?,
    @JsonProperty("started_at") var startedAt: String?,
    @JsonProperty("language") var language: String?,
    @JsonProperty("thumbnail_url") var thumbnailUrl: String?,
    @JsonProperty("tag_ids") var tagIds: List<String>?
)
