package com.cwtsite.cwt.test

import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito

object MockitoUtils {

    /** [https://stackoverflow.com/a/30308199/2015430](https://stackoverflow.com/a/30308199/2015430) */
    fun <T> anyObject(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    fun <T : Any> safeEq(value: T): T = eq(value) ?: value

    @Suppress("UNCHECKED_CAST")
    private fun <T> uninitialized(): T = null as T
}
