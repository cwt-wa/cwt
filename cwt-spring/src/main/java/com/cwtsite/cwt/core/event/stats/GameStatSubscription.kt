package com.cwtsite.cwt.core.event.stats

class GameStatSubscription(val gameId: Long, val callback: (String, Boolean) -> Unit)
