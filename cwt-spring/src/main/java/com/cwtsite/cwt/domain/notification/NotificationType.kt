package com.cwtsite.cwt.domain.notification

enum class NotificationType(private val pos: Int, private val label: String) {
    PUBLIC_CHAT(1, "Public chat messages"),
    PRIVATE_MESSAGE(2, "Private messages"),
    REPORTED_GAME(3, "New game reported"),
    VOIDED_GAME(4, "Game voided"),
    RATED_GAME(5, "Game rated"),
    COMMENTED_GAME(6, "Game commented"),
    SCHEDULED_GAME(7, "Game rated"),
    SCHEDULED_LIVE_STREAM(8, "Live Stream scheduled"),
    RECORDED_LIVE_STREAM(9, "New Live Stream recorded"),
    LIVE_STREAM_ONLINE(10, "Live Stream channel gone live (not yet implemented)"),
    ANNOUNCEMENTS(11, "Announcements (not yet implemented)");

    fun on(i: Int): Boolean = Integer.toBinaryString(i).reversed()[pos] == '1'
}