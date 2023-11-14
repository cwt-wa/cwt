package com.cwtsite.cwt.domain.notification.service

import org.json.JSONArray
import org.json.JSONObject

data class PushNotification(
    val title: String,
    val body: String? = null,
    val tag: String? = null,
) {
    fun toRequest(subscriptions: List<String>): Map<String, Any> = mapOf(
        "subs" to JSONArray(subscriptions.map { JSONObject(it) }),
        "notification" to mapOf(
            "title" to title,
            "options" to mapOf(
                "body" to body,
                "tag" to tag,
            )
        )
    )
}
