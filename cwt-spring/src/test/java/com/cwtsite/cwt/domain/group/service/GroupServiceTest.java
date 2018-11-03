package com.cwtsite.cwt.domain.group.service;

import com.cwtsite.cwt.domain.configuration.service.ConfigurationService;
import com.cwtsite.cwt.domain.game.entity.Game;
import com.cwtsite.cwt.domain.group.entity.Group;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import com.cwtsite.cwt.entity.GroupStanding;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class GroupServiceTest {

    @InjectMocks
    private GroupService groupService;

    @Mock
    private GroupStandingRepository groupStandingRepository;

    @Mock
    private ConfigurationService configurationService;

    @Test
    public void calcTableByGame() {
        Game game = new Game();

        final User user1 = createUser(1);
        final User user2 = createUser(2);

        game.setHomeUser(user1);
        game.setAwayUser(user2);
        game.setScoreHome(3);
        game.setScoreAway(2);
        game.setGroup(createGroup(user1, user2));

        Mockito
                .when(configurationService.getParsedPointsPatternConfiguration())
                .thenReturn(Arrays.asList(new int[]{3, 3}, new int[]{2, 1}));

        Mockito
                .when(groupStandingRepository.saveAll(Mockito.anyCollection()))
                .thenAnswer(invocation -> {
                    final List<GroupStanding> actualStandings = invocation.getArgument(0);
                    final GroupStanding winnerStandings = actualStandings.get(0);
                    final GroupStanding loserStandings = actualStandings.get(1);

                    Assert.assertEquals(user1, winnerStandings.getUser());
                    Assert.assertEquals(2, (int) winnerStandings.getGames());
                    Assert.assertEquals(6, (int) winnerStandings.getPoints());
                    Assert.assertEquals(2, (int) winnerStandings.getGameRatio());
                    Assert.assertEquals(4, (int) winnerStandings.getRoundRatio());

                    Assert.assertEquals(user2, loserStandings.getUser());
                    Assert.assertEquals(2, (int) loserStandings.getGames());
                    Assert.assertEquals(4, (int) loserStandings.getPoints());
                    Assert.assertEquals(0, (int) loserStandings.getGameRatio());
                    Assert.assertEquals(2, (int) loserStandings.getRoundRatio());

                    return actualStandings;
                });

        groupService.calcTableByGame(game);
    }

    private Group createGroup(User user1, User user2) {
        final Group group = new Group();
        group.setStandings(Arrays.asList(createGroupStanding(user1, group), createGroupStanding(user2, group)));
        return group;
    }

    private GroupStanding createGroupStanding(User user, Group group) {
        final GroupStanding groupStanding = new GroupStanding(group, user);
        groupStanding.setUser(user);
        groupStanding.setGames(1);
        groupStanding.setPoints(3);
        groupStanding.setGameRatio(1);
        groupStanding.setRoundRatio(3);
        return groupStanding;
    }

    private User createUser(long id) {
        final User user = new User();
        user.setId(id);
        return user;
    }
}
