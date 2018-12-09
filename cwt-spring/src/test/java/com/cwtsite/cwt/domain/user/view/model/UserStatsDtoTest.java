package com.cwtsite.cwt.domain.user.view.model;

import org.junit.Assert;
import org.junit.Test;

public class UserStatsDtoTest {

    @Test
    public void locRound() {
        Assert.assertEquals(UserStatsDto.locRound(5, 7), "Champion");
        Assert.assertEquals(UserStatsDto.locRound(5, 6), "Runner-up");
        Assert.assertEquals(UserStatsDto.locRound(5, 5), "Third");
        Assert.assertEquals(UserStatsDto.locRound(5, 4), "Semifinal");
        Assert.assertEquals(UserStatsDto.locRound(5, 3), "Quarterfinal");
        Assert.assertEquals(UserStatsDto.locRound(5, 2), "Last Sixteen");
        Assert.assertEquals(UserStatsDto.locRound(5, 1), "Group");
        Assert.assertEquals(UserStatsDto.locRound(5, 0), "Not attended");
        Assert.assertEquals(UserStatsDto.locRound(6, 1), "Group");
        Assert.assertEquals(UserStatsDto.locRound(6, 3), "Last Sixteen");
        Assert.assertEquals(UserStatsDto.locRound(6, 2), "Last 32");
        Assert.assertEquals(UserStatsDto.locRound(7, 2), "Last 64");
        Assert.assertEquals(UserStatsDto.locRound(7, 3), "Last 32");
    }
}
