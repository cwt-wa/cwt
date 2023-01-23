package com.cwtsite.cwt.domain.ranking.entity

import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus.ARCHIVED
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus.GROUP
import com.cwtsite.cwt.test.EntityDefaults.tournament
import com.cwtsite.cwt.test.EntityDefaults.user
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class RankingTest {

    @Test
    fun diff() {
        // initial no archived tournaments existing
        Ranking(
            user = user(),
            lastPlace = 0,
            lastDiff = 0,
        ).also {
            it.diff(
                newPlace = 1,
                newRef = null,
                prevRef = null,
                prevLastPlace = null,
                max = 5,
            )
            assertThat(it.lastPlace).isEqualTo(1)
            assertThat(it.lastDiff).isEqualTo(-4)
        }
        // again no archived tournaments existing
        Ranking(
            user = user(),
            lastPlace = 1,
            lastDiff = -4,
        ).also {
            it.diff(
                newPlace = 2,
                newRef = null,
                prevRef = null,
                prevLastPlace = it.lastPlace,
                max = 7,
            )
            assertThat(it.lastPlace).isEqualTo(2)
            assertThat(it.lastDiff).isEqualTo(1)
        }
        // tournament gets archived
        Ranking(
            user = user(),
            lastPlace = 2,
            lastDiff = 1,
        ).also {
            it.diff(
                newPlace = 3,
                newRef = tournament(id = 1, status = ARCHIVED),
                prevRef = null,
                prevLastPlace = it.lastPlace,
                max = 32,
            )
            assertThat(it.lastPlace).isEqualTo(3)
            assertThat(it.lastDiff).isEqualTo(0)
        }
        // during next tournament
        Ranking(
            user = user(),
            lastPlace = 3,
            lastDiff = 0,
            lastTournament = tournament(id = 2, status = GROUP),
        ).also {
            it.diff(
                newPlace = 10,
                newRef = tournament(id = 1, status = ARCHIVED),
                prevRef = tournament(id = 1, status = ARCHIVED),
                prevLastPlace = it.lastPlace,
                max = 35,
            )
            assertThat(it.lastPlace).isEqualTo(3)
            assertThat(it.lastDiff).isEqualTo(7)
        }
        // again during tournament
        Ranking(
            user = user(),
            lastPlace = 3,
            lastDiff = 7,
            lastTournament = tournament(id = 2, status = GROUP),
        ).also {
            it.diff(
                newPlace = 0,
                newRef = tournament(id = 1, status = ARCHIVED),
                prevRef = tournament(id = 1, status = ARCHIVED),
                prevLastPlace = it.lastPlace,
                max = 42,
            )
            assertThat(it.lastPlace).isEqualTo(3)
            assertThat(it.lastDiff).isEqualTo(-3)
        }
        // tournament get archived
        Ranking(
            user = user(),
            lastPlace = 3,
            lastDiff = -3,
            lastTournament = tournament(id = 2, status = ARCHIVED),
        ).also {
            it.diff(
                newPlace = 1,
                newRef = tournament(id = 2, status = ARCHIVED),
                prevRef = tournament(id = 1, status = ARCHIVED),
                prevLastPlace = it.lastPlace,
                max = 60,
            )
            assertThat(it.lastPlace).isEqualTo(1)
            assertThat(it.lastDiff).isEqualTo(-2)
        }
    }
}
