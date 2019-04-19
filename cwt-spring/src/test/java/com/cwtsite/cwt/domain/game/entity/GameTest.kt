package com.cwtsite.cwt.domain.game.entity

import com.cwtsite.cwt.test.EntityDefaults
import org.assertj.core.api.Assertions
import org.junit.Test

class GameTest {

    @Test
    fun isPlayed() {
        val game1 = EntityDefaults.game()
        Assertions.assertThat(game1.isPlayed()).isTrue()
        Assertions.assertThat(game1.winner()).isEqualTo(game1.homeUser)
        Assertions.assertThat(game1.loser()).isEqualTo(game1.awayUser)

        val game2 = EntityDefaults.game(homeUser = null, awayUser = null, scoreHome = null, scoreAway = null)
        Assertions.assertThat(game2.isPlayed()).isFalse()
        Assertions.assertThatThrownBy { game2.winner() }.isExactlyInstanceOf(KotlinNullPointerException::class.java)
        Assertions.assertThatThrownBy { game2.loser() }.isExactlyInstanceOf(KotlinNullPointerException::class.java)
    }
}
