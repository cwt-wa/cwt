package com.cwtsite.cwt.domain.core

class WrappedCloseable<T>(val content: T, private val closer: Function0<Unit>) : AutoCloseable {

    override fun close() {
        closer.invoke()
    }
}
