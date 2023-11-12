package com.cwtsite.cwt.domain.notification

enum class NotificationType(
    private val pos: Int,
    private val label: String,
    public val title: String,
    public val tag: String
) {
    PUBLIC_CHAT(1, "Public chat messages", "New Chat Message", "chat"),
    PRIVATE_MESSAGE(2, "Private messages", "New Private Message", "pm"),
    REPORTED_GAME(3, "New game reported", "New Game Report", "report"),
    VOIDED_GAME(4, "Game voided", "Game Voided", "void"),
    RATED_GAME(5, "Game rated", "Game Rated", "rating"),
    COMMENTED_GAME(6, "Game commented", "Game Commented", "comment"),
    SCHEDULED_GAME(7, "Game scheduled", "Game Scheduled", "schedule"),
    SCHEDULED_LIVE_STREAM(8, "Live Stream scheduled", "Live Stream scheduled", "stream_schedule"),
    RECORDED_LIVE_STREAM(9, "New Live Stream recorded", "Live Stream recorded", "stream_record"),
    LIVE_STREAM_ONLINE(10, "Live Stream channel gone live (not yet implemented)", "Live Stream went live", "stream_live"),
    ANNOUNCEMENTS(11, "Announcements (not yet implemented)", "New Announcement", "announcement");

    fun on(i: Int): Boolean = Integer.toBinaryString(i).reversed()[pos] == '1'

    fun tag(postfix: String) = "$tag-$postfix"
}