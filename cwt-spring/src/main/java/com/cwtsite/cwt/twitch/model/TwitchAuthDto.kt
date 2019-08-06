package com.cwtsite.cwt.twitch.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.fasterxml.jackson.annotation.JsonProperty

@DataTransferObject
data class TwitchAuthDto(
        @JsonProperty("access_token") val accessToken: String?,
        @JsonProperty("refresh_token") val refreshToken: String?,
        @JsonProperty("expires_in") val expiresIn: Long?,
        @JsonProperty("scope") val scope: List<String>?,
        @JsonProperty("token_type") val tokenType: String?
)
