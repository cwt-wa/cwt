package com.cwtsite.cwt.twitch.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.fasterxml.jackson.annotation.JsonProperty

@DataTransferObject
data class TwitchVideoDto(
        @JsonProperty("id") var id: String,
        @JsonProperty("user_id") var userId: String,
        @JsonProperty("user_name") var userName: String?,
        @JsonProperty("title") var title: String?,
        @JsonProperty("description") var description: String?,
        @JsonProperty("created_at") var createdAt: String?,
        @JsonProperty("published_at") var publishedAt: String?,
        @JsonProperty("url") var url: String?,
        @JsonProperty("thumbnail_url") var thumbnailUrl: String?,
        @JsonProperty("viewable") var viewable: String?,
        @JsonProperty("view_count") var viewCount: Long,
        @JsonProperty("language") var language: String?,
        @JsonProperty("type") var type: String?,
        @JsonProperty("duration") var duration: String?
) {

    fun hasCwtInTitle(): Boolean = title?.contains(Regex("""\bcwt\b""", RegexOption.IGNORE_CASE)) ?: false
}
