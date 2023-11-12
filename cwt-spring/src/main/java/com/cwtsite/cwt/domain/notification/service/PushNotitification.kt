package com.cwtsite.cwt.domain.notification.service

data class PushNotification(
    val title: String,
    val body: String? = null,
    val tag: String? = null,
) {
    fun toRequest(subscriptions: List<String>): Map<String, Any> = mapOf(
        "subs" to subscriptions,
        "notification" to mapOf(
            "title" to title,
            "options" to mapOf(
                "body" to body,
                "tag" to tag,
            )
        )
    )
}
