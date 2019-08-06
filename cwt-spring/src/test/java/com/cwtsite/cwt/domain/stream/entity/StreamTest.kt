package com.cwtsite.cwt.domain.stream.entity

import org.assertj.core.api.Assertions
import org.mockito.Mockito
import kotlin.test.Test

class StreamTest {

    @Test
    fun hasCwtInTitle() {
        val stream = Mockito.spy(Stream::class.java)

        stream.title = "CWT 2018, Semi Final, Chuvash vs Boolc"
        Assertions.assertThat(stream.hasCwtInTitle()).isTrue()

        stream.title = "Replay analysis [russian]. CWT'14. Last 16. Johnmir - Viks."
        Assertions.assertThat(stream.hasCwtInTitle()).isTrue()

        stream.title = "CWT'14. Semi-final. Terror - chuvash"
        Assertions.assertThat(stream.hasCwtInTitle()).isTrue()

        stream.title = "Some Epic Game That Is Of Another Game But IncludesCWT somehow"
        Assertions.assertThat(stream.hasCwtInTitle()).isFalse()

        stream.title = "Something entirely else"
        Assertions.assertThat(stream.hasCwtInTitle()).isFalse()
    }
}
