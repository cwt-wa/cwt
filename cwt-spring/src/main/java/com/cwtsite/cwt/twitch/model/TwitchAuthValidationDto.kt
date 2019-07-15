package com.cwtsite.cwt.twitch.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.fasterxml.jackson.annotation.JsonProperty

@DataTransferObject
data class TwitchAuthValidationDto(
        @JsonProperty("client_id") val clientid: String,
        @JsonProperty("scopes") val scopes: List<String>
)
