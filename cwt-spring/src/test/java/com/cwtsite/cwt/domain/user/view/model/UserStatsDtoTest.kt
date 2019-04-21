package com.cwtsite.cwt.domain.user.view.model

import org.assertj.core.api.Assertions
import org.junit.Test

class UserStatsDtoTest {

    @Test
    fun toDtos() {
        Assertions
                .assertThat(UserStatsDto.toDtos("[101,2001,5,7],[102,2002,5,6],[103,2003,5,5],[104,2004,5,4],[105,2005,5,3],[106,2006,5,2],[106,2006,5,1],[107,2007,5,0],[108,2008,6,1],[108,2008,6,3],[109,2009,6,2],[110,2010,7,2],[111,2011,7,3]")
                        .map { it.locRound })
                .containsExactly(
                        "Champion",
                        "Runner-up",
                        "Third",
                        "Semifinal",
                        "Quarterfinal",
                        "Last Sixteen",
                        "Group",
                        "Not attended",
                        "Group",
                        "Last Sixteen",
                        "Last 32",
                        "Last 64",
                        "Last 32")
    }
}
