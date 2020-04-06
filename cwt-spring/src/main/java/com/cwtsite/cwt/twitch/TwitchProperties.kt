package com.cwtsite.cwt.twitch

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("twitch")
class TwitchProperties {

    /**
     * Max reults per pagination cursor.
     */
    val resultLimit: Int? = 100

    /**
     * The interval in which a request can succeed a previous in seconds.
     */
    val resultInterval: Int? = 10

    /**
     * The URL tot the Twitch API.
     */
    var url: String? = "https://api.twitch.tv/helix/"

    /**
     * The app’s client ID which can be obtained from the Twitch developer dashboard.
     */
    var clientId: String? = null

    /**
     * The app’s client secret which can be obtained from the Twitch developer dashboard.
     */
    var clientSecret: String? = null

    /**
     * The `Authorization` header’s name.
     */
    var authorizationHeaderName: String? = "Authorization"

    /**
     * The URL to hit to obtain an authentication token.
     */
    var authUrl: String? = "https://id.twitch.tv/oauth2/token"

    /**
     * The URL to hit to validate an authentication token.
     */
    var authValidateUrl: String? = "https://id.twitch.tv/oauth2/validate"

    /**
     * The endpoint for what the Twitch API calls `videos`.
     */
    var videosEndpoint: String? = "videos"

    /**
     * The endpoint for what the Twitch API calls `streams`.
     */
    var streamsEndpoint: String? = "streams"

    /**
     * The endpoint for what the Twitch API calls `users`.
     */
    var usersEndpoint: String? = "users"

    /**
     * Milliseconds that must have gone by until new data is queried from Twitch.
     */
    var millisToRequest: Long? = 5000
}

