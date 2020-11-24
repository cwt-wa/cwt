package com.cwtsite.cwt.test

import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito

object MockitoUtils {

    /** [https://stackoverflow.com/a/30308199/2015430](https://stackoverflow.com/a/30308199/2015430) */
    fun <T> anyObject(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    fun <T : Any> safeEq(value: T): T = eq(value) ?: value

    fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()


    fun <T> safeArgThat(matcher: ArgumentMatcher<T>): T {
        ArgumentMatchers.argThat(matcher)
        return uninitialized()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> uninitialized(): T = null as T
}
