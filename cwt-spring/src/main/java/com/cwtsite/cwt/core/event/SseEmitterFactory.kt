package com.cwtsite.cwt.core.event

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException

/** This is so that the SseEmitter can be mocked in tests. */
@Service
class SseEmitterFactory {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun createInstance(timeout: Long = -1L): SseEmitter = SseEmitter(timeout)

    fun runCatchingBrokenPipe(block: () -> Unit): Boolean =
            try {
                block()
                false
            } catch (e: IOException) {
                if (e.message == "Broken pipe") {
                    logger.info("Broken pipe, probably the user has just left. Completing the emitter.")
                    false
                } else {
                    true
                }
            }
}
