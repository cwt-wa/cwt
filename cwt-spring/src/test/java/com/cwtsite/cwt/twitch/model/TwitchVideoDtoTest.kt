package com.cwtsite.cwt.twitch.model

import org.assertj.core.api.Assertions
import org.junit.Test
import org.mockito.Mockito

class TwitchVideoDtoTest {

    @Test
    fun hasCwtInTitle() {
        val twitchVideoDto = Mockito.spy(TwitchVideoDto::class.java)

        twitchVideoDto.title = "CWT 2018, Semi Final, Chuvash vs Boolc"
        Assertions.assertThat(twitchVideoDto.hasCwtInTitle()).isTrue()

        twitchVideoDto.title = "Replay analysis [russian]. CWT'14. Last 16. Johnmir - Viks."
        Assertions.assertThat(twitchVideoDto.hasCwtInTitle()).isTrue()

        twitchVideoDto.title = "CWT'14. Semi-final. Terror - chuvash"
        Assertions.assertThat(twitchVideoDto.hasCwtInTitle()).isTrue()

        twitchVideoDto.title = "Some Epic Game That Is Of Another Game But IncludesCWT somehow"
        Assertions.assertThat(twitchVideoDto.hasCwtInTitle()).isFalse()

        twitchVideoDto.title = "Something entirely else"
        Assertions.assertThat(twitchVideoDto.hasCwtInTitle()).isFalse()
    }
}
