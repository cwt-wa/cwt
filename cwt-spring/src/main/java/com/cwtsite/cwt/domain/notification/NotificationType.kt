package com.cwtsite.cwt.domain.notification

import com.cwtsite.cwt.core.toInt

private const val MAX = 100

enum class NotificationType(
    val pos: Int,
    val label: String,
    val title: String,
    val tag: String
) {
    PUBLIC_CHAT(
        MAX,
        "Public chat messages",
        "New Chat Message",
        "chat"
    ),
    PRIVATE_MESSAGE(
        99,
        "Private messages",
        "New Private Message",
        "pm"
    ),
    REPORTED_GAME(
        98,
        "New game reported",
        "New Game Report",
        "report"
    ),
    VOIDED_GAME(
        97,
        "Game voided",
        "Game Voided",
        "void"
    ),
    RATED_GAME(
        96,
        "Game rated",
        "Game Rated",
        "rating"
    ),
    COMMENTED_GAME(
        95,
        "Game commented",
        "Game Commented",
        "comment"
    ),
    SCHEDULED_GAME(
        94,
        "Game scheduled",
        "Game Scheduled",
        "schedule"
    ),
    SCHEDULED_LIVE_STREAM(
        93,
        "Live Stream scheduled",
        "Live Stream scheduled",
        "stream_schedule"
    ),
    RECORDED_LIVE_STREAM(
        92,
        "New Live Stream recorded",
        "Live Stream recorded",
        "stream_record"
    ),
    LIVE_STREAM_ONLINE(
        91,
        "Live Stream channel gone live (not yet implemented)",
        "Live Stream went live",
        "stream_live"
    ),
    ANNOUNCEMENTS(
        90,
        "Announcements (not yet implemented)",
        "New Announcement",
        "announcement"
    );

    companion object {
        fun fromSetting(positions: List<Int>, values: List<Boolean>): Int =
            positions.zip(values)
                .sortedBy { it.first }
                .joinToString(separator = "", transform = { it.second.toInt().toString() })
                .toInt(2)
    }

    fun on(i: Int): Boolean =
        Integer.toBinaryString(i).padStart(MAX, '0')[pos-1] == '1'

    fun tag(postfix: String) = "$tag-$postfix"
}
