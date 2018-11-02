package com.cwtsite.cwt.domain.game.service;

import com.cwtsite.cwt.domain.configuration.entity.Configuration;
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey;
import com.cwtsite.cwt.domain.configuration.service.ConfigurationService;
import com.cwtsite.cwt.domain.game.entity.Game;
import com.cwtsite.cwt.domain.group.entity.Group;
import com.cwtsite.cwt.domain.group.entity.enumeration.GroupLabel;
import com.cwtsite.cwt.domain.group.service.GroupRepository;
import com.cwtsite.cwt.domain.group.service.GroupService;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus;
import com.cwtsite.cwt.domain.tournament.service.TournamentService;
import com.cwtsite.cwt.domain.user.repository.UserRepository;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import com.cwtsite.cwt.domain.user.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceTest {

    @InjectMocks
    private GameService gameService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private TournamentService tournamentService;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GroupService groupService;

    @Test
    public void reportGame() {
        final long homeUserId = 1;
        final long awayUserId = 3;
        final Tournament tournament = createTournament();

        Mockito
                .when(tournamentService.getCurrentTournament())
                .thenReturn(tournament);

        Mockito
                .when(configurationService.getBestOfValue(TournamentStatus.GROUP))
                .thenReturn(createGroupGameBestOfConfiguration());

        try {
            gameService.reportGame(homeUserId, awayUserId, 3, 1);
            Assert.fail();
        } catch (GameService.InvalidScoreException ignored) {
        }

        try {
            gameService.reportGame(homeUserId, awayUserId, 0, 1);
            Assert.fail();
        } catch (GameService.InvalidScoreException ignored) {
        }

        final User awayUser = createUser(awayUserId);
        final User homeUser = createUser(homeUserId);

        Mockito
                .when(userService.getRemainingOpponents(Mockito.any()))
                .thenReturn(Collections.singletonList(createUser(99)))
                .thenReturn(Collections.singletonList(awayUser));

        Mockito
                .when(userRepository.findById(homeUserId))
                .thenReturn(Optional.of(homeUser));

        Mockito
                .when(userRepository.findById(awayUserId))
                .thenReturn(Optional.of(awayUser));

        try {
            gameService.reportGame(homeUserId, awayUserId, 1, 2);
            Assert.fail();
        } catch (GameService.InvalidOpponentException ignored) {
        }

        final Group group = createGroup(tournament);

        Mockito
                .when(groupRepository.findByTournamentAndUser(tournament, awayUser))
                .thenReturn(group);

        final int expectedScoreHome = 1;
        final int expectedScoreAway = 2;

        Mockito
                .when(gameRepository.save(Mockito.any()))
                .thenAnswer(invocation -> {
                    final Game actualGame = invocation.getArgument(0);

                    Assert.assertEquals(awayUser, actualGame.getAwayUser());
                    Assert.assertEquals(homeUser, actualGame.getHomeUser());
                    Assert.assertEquals(group, actualGame.getGroup());
                    Assert.assertEquals(group.getLabel(), actualGame.getGroup().getLabel());
                    Assert.assertEquals(group.getTournament(), actualGame.getGroup().getTournament());
                    Assert.assertEquals(expectedScoreHome, (int) actualGame.getScoreHome());
                    Assert.assertEquals(expectedScoreAway, (int) actualGame.getScoreAway());
                    Assert.assertEquals(expectedScoreAway, (int) actualGame.getScoreAway());
                    Assert.assertNull(actualGame.getPlayoff());
                    Assert.assertFalse(actualGame.isTechWin());

                    return actualGame;
                });

        gameService.reportGame(homeUserId, awayUserId, expectedScoreHome, expectedScoreAway);
    }

    private Group createGroup(Tournament tournament) {
        final Group group = new Group();
        group.setId(77L);
        group.setTournament(tournament);
        group.setLabel(GroupLabel.G);
        return group;
    }

    private User createUser(long userId) {
        final User user = new User();
        user.setId(userId);
        return user;
    }

    private Tournament createTournament() {
        final Tournament tournament = new Tournament();
        tournament.setId(99L);
        tournament.setStatus(TournamentStatus.GROUP);
        return tournament;
    }

    private Configuration createGroupGameBestOfConfiguration() {
        final Configuration configuration = new Configuration();
        configuration.setKey(ConfigurationKey.GROUP_GAMES_BEST_OF);
        configuration.setValue("3");
        return configuration;
    }
}
