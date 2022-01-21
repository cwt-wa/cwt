package com.cwtsite.cwt.core.event

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import kotlin.concurrent.fixedRateTimer

/** This is so that the SseEmitter can be mocked in tests. */
@Service
class SseEmitterFactory {

    fun createInstance(timeout: Long = -1L): SseEmitterWrapper = SseEmitterWrapper(SseEmitter(timeout))
}

class SseEmitterWrapper(val delegate: SseEmitter) {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val cancelHeartbeat: () -> Unit

    init {
        val heartbeat = fixedRateTimer(period = 10000) {
            send("KEEPALIVE", "KEEPALIVE")
        }
        cancelHeartbeat = { heartbeat.cancel() }
        delegate.onCompletion(cancelHeartbeat)
    }

    fun send(name: String, data: Any) {
        runCatching {
            delegate.send(SseEmitter.event().data(data).name(name))
        }.onFailure {
            if (it is IOException && it.message == "Broken pipe") {
                logger.info("Broken pipe, probably the user has just left. Completing the emitter.")
            }
        }
    }

    fun onCompletion(cb: () -> Unit) {
        delegate.onCompletion {
            cancelHeartbeat()
            cb()
        }
    }

    fun onTimeout(cb: () -> Unit) = delegate.onTimeout(cb)

    fun onError(cb: (throwable: Throwable) -> Unit) = delegate.onError(cb)

    fun complete() = delegate.complete()
}
