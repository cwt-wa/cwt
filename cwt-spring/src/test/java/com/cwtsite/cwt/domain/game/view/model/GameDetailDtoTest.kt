package com.cwtsite.cwt.domain.game.view.model

import org.assertj.core.api.Assertions
import org.junit.Test

class GameDetailDtoTest {

    @Test
    fun localizePlayoffRound() {
        Assertions
            .assertThat(GameDetailDto.localizePlayoffRound(threeWayFinal = false, playoffsRoundMax = 4, achievedRound = 1))
            .isEqualTo("Last 16")

        Assertions
            .assertThat(GameDetailDto.localizePlayoffRound(threeWayFinal = false, playoffsRoundMax = 4, achievedRound = 2))
            .isEqualTo("Quarterfinal")

        Assertions
            .assertThat(GameDetailDto.localizePlayoffRound(threeWayFinal = false, playoffsRoundMax = 4, achievedRound = 3))
            .isEqualTo("Semifinal")

        Assertions
            .assertThat(GameDetailDto.localizePlayoffRound(threeWayFinal = false, playoffsRoundMax = 4, achievedRound = 4))
            .isEqualTo("Little Final")

        Assertions
            .assertThat(GameDetailDto.localizePlayoffRound(threeWayFinal = false, playoffsRoundMax = 4, achievedRound = 5))
            .isEqualTo("Final")

        Assertions
            .assertThat(GameDetailDto.localizePlayoffRound(threeWayFinal = true, playoffsRoundMax = 4, achievedRound = 4))
            .isEqualTo("Three-way Final")

        Assertions
            .assertThat(GameDetailDto.localizePlayoffRound(threeWayFinal = true, playoffsRoundMax = 2, achievedRound = 1))
            .isEqualTo("Last 6")
    }
}
