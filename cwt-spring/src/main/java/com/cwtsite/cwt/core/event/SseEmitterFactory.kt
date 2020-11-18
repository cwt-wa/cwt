package com.cwtsite.cwt.core.event

import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

/** This is so that the SseEmitter can be mocked in tests. */
@Service
class SseEmitterFactory {
    fun createInstance(timeout: Long = -1L): SseEmitter = SseEmitter(timeout)
}
