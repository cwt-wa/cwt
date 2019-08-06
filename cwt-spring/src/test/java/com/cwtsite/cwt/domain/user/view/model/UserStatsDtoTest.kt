package com.cwtsite.cwt.domain.user.view.model

import org.assertj.core.api.Assertions
import org.junit.Test

class UserStatsDtoTest {

    @Test
    fun toDtos() {
        // 8 players in playoffs
        Assertions
                .assertThat(UserStatsDto.toDtos("[0,0,0,4,6],[0,0,0,4,5],[0,0,0,4,4],[0,0,0,4,3],[0,0,0,4,2],[0,0,0,4,1],[0,0,0,4,0]")
                        .map { it.locRound })
                .containsExactly(
                        "Champion",
                        "Runner-up",
                        "Third",
                        "Semifinal",
                        "Quarterfinal",
                        "Group",
                        "Not attended")

        // 12 players in playoffs with three-way final
        Assertions
                .assertThat(UserStatsDto.toDtos("[0,0,1,3,6],[0,0,1,3,5],[0,0,1,3,4],[0,0,1,3,3],[0,0,1,3,2],[0,0,1,3,1],[0,0,1,3,0]")
                        .map { it.locRound })
                .containsExactly(
                        "Champion",
                        "Runner-up",
                        "Third",
                        "Last 6",
                        "Last 12",
                        "Group",
                        "Not attended")

        // 16 players in playoffs
        Assertions
                .assertThat(UserStatsDto.toDtos("[0,0,0,5,7],[0,0,0,5,6],[0,0,0,5,5],[0,0,0,5,4],[0,0,0,5,3],[0,0,0,5,2],[0,0,0,5,1],[0,0,0,5,0]")
                        .map { it.locRound })
                .containsExactly(
                        "Champion",
                        "Runner-up",
                        "Third",
                        "Semifinal",
                        "Quarterfinal",
                        "Last 16",
                        "Group",
                        "Not attended")

        // 24 players in playoffs with three-way final
        Assertions
                .assertThat(UserStatsDto.toDtos("[0,0,1,4,7],[0,0,1,4,6],[0,0,1,4,5],[0,0,1,4,4],[0,0,1,4,3],[0,0,1,4,2],[0,0,1,4,1],[0,0,1,4,0]")
                        .map { it.locRound })
                .containsExactly(
                        "Champion",
                        "Runner-up",
                        "Third",
                        "Last 6",
                        "Last 12",
                        "Last 24",
                        "Group",
                        "Not attended")
    }
}
