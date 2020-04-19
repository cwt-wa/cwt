package com.cwtsite.cwt.core

import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ClockInstance {
    val now: Instant get() = Instant.now()
}
