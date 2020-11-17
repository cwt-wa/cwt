package com.cwtsite.cwt.domain.message.service

enum class MessageNewsType {
    REPORT, RATING, COMMENT, VOIDED, STREAM,
    TWITCH_MESSAGE, DISCORD_MESSAGE;

    companion object {

        fun thirdPartyMessageTypes(): Set<MessageNewsType> =
                setOf(TWITCH_MESSAGE, DISCORD_MESSAGE)
    }
}
