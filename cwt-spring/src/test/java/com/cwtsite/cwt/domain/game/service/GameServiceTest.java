package com.cwtsite.cwt.domain.game.service;

import com.cwtsite.cwt.domain.configuration.entity.Configuration;
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey;
import com.cwtsite.cwt.domain.configuration.service.ConfigurationService;
import com.cwtsite.cwt.domain.game.entity.Game;
import com.cwtsite.cwt.domain.game.entity.PlayoffGame;
import com.cwtsite.cwt.domain.group.entity.Group;
import com.cwtsite.cwt.domain.group.entity.enumeration.GroupLabel;
import com.cwtsite.cwt.domain.group.service.GroupRepository;
import com.cwtsite.cwt.domain.group.service.GroupService;
import com.cwtsite.cwt.domain.playoffs.service.PlayoffService;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus;
import com.cwtsite.cwt.domain.tournament.exception.IllegalTournamentStatusException;
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

    @Mock
    private PlayoffService playoffService;

    @Test
    public void reportGameForGroupStage() {
        final long homeUserId = 1;
        final long awayUserId = 3;
        final Tournament tournament = createTournament(TournamentStatus.GROUP);

        final User awayUser = createUser(awayUserId);
        final User homeUser = createUser(homeUserId);

        mockAndAssertValidationHappeningBeforeActualReport(homeUserId, awayUserId, tournament, awayUser, homeUser);

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

                    assertIndependentOfTournamentStatus(
                            awayUser, homeUser, expectedScoreHome, expectedScoreAway, actualGame, tournament);
                    Assert.assertEquals(group, actualGame.getGroup());
                    Assert.assertEquals(group.getLabel(), actualGame.getGroup().getLabel());
                    Assert.assertEquals(group.getTournament(), actualGame.getGroup().getTournament());
                    Assert.assertFalse(actualGame.isTechWin());
                    Assert.assertNull(actualGame.getPlayoff());
                    Assert.assertNotNull(actualGame.getGroup());

                    return actualGame;
                });

        gameService.reportGame(homeUserId, awayUserId, expectedScoreHome, expectedScoreAway);
    }

    @Test
    public void reportGameForPlayoffs() {
        final long homeUserId = 1;
        final long awayUserId = 3;
        final Tournament tournament = createTournament(TournamentStatus.PLAYOFFS);

        final User awayUser = createUser(awayUserId);
        final User homeUser = createUser(homeUserId);

        mockAndAssertValidationHappeningBeforeActualReport(homeUserId, awayUserId, tournament, awayUser, homeUser);

        final int expectedScoreHome = 1;
        final int expectedScoreAway = 2;

        Mockito
                .when(gameRepository.findNextPlayoffGameForUser(Mockito.any(), Mockito.any()))
                .thenAnswer(invocation -> {
                    final Game game = new Game();
                    game.setTournament(invocation.getArgument(0));
                    game.setHomeUser(invocation.getArgument(1));
                    game.setAwayUser(awayUser);
                    game.setPlayoff(new PlayoffGame());
                    return game;
                })
                .thenAnswer(invocation -> {
                    final Game game = new Game();
                    game.setTournament(invocation.getArgument(0));
                    game.setHomeUser(awayUser);
                    game.setAwayUser(invocation.getArgument(1));
                    game.setPlayoff(new PlayoffGame());
                    return game;
                })
                .thenAnswer(invocation -> {
                    final Game game = new Game();
                    game.setTournament(invocation.getArgument(0));
                    game.setHomeUser(invocation.getArgument(1));
                    game.setAwayUser(createUser(19));
                    game.setPlayoff(new PlayoffGame());
                    return game;
                });

        //noinspection Duplicates
        Mockito
                .when(gameRepository.save(Mockito.any()))
                .thenAnswer(invocation -> {
                    final Game actualGame = invocation.getArgument(0);

                    assertIndependentOfTournamentStatus(
                            awayUser, homeUser, expectedScoreHome, expectedScoreAway, actualGame, tournament);
                    Assert.assertFalse(actualGame.isTechWin());
                    Assert.assertNull(actualGame.getGroup());
                    Assert.assertNotNull(actualGame.getPlayoff());

                    return actualGame;
                })
                .thenAnswer(invocation -> {
                    final Game actualGame = invocation.getArgument(0);

                    assertIndependentOfTournamentStatus(
                            homeUser, awayUser, expectedScoreAway, expectedScoreHome, actualGame, tournament);
                    Assert.assertFalse(actualGame.isTechWin());
                    Assert.assertNull(actualGame.getGroup());
                    Assert.assertNotNull(actualGame.getPlayoff());

                    return actualGame;
                });

        gameService.reportGame(homeUserId, awayUserId, expectedScoreHome, expectedScoreAway);
        gameService.reportGame(homeUserId, awayUserId, expectedScoreHome, expectedScoreAway);
        try {
            gameService.reportGame(homeUserId, awayUserId, expectedScoreHome, expectedScoreAway);
            Assert.fail();
        } catch (GameService.InvalidOpponentException ignored) {
        }
    }

    private void assertIndependentOfTournamentStatus(
            User awayUser, User homeUser, int expectedScoreHome, int expectedScoreAway, Game actualGame,
            Tournament expectedTournament) {
        Assert.assertEquals(awayUser, actualGame.getAwayUser());
        Assert.assertEquals(homeUser, actualGame.getHomeUser());
        Assert.assertEquals(expectedScoreHome, (int) actualGame.getScoreHome());
        Assert.assertEquals(expectedScoreAway, (int) actualGame.getScoreAway());
        Assert.assertEquals(expectedScoreAway, (int) actualGame.getScoreAway());
        Assert.assertEquals(expectedScoreAway, (int) actualGame.getScoreAway());
        Assert.assertEquals(expectedTournament, actualGame.getTournament());
    }

    private void mockAndAssertValidationHappeningBeforeActualReport(long homeUserId, long awayUserId, Tournament tournament, User awayUser, User homeUser) {
        Mockito
                .when(tournamentService.getCurrentTournament())
                .thenReturn(tournament);

        Mockito
                .when(configurationService.getBestOfValue(TournamentStatus.GROUP))
                .thenReturn(createGroupGameBestOfConfiguration(ConfigurationKey.GROUP_GAMES_BEST_OF));

        Mockito
                .when(configurationService.getBestOfValue(TournamentStatus.PLAYOFFS))
                .thenReturn(createGroupGameBestOfConfiguration(ConfigurationKey.PLAYOFF_GAMES_BEST_OF));

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

    private Tournament createTournament(TournamentStatus tournamentStatus) {
        final Tournament tournament = new Tournament();
        tournament.setId(99L);
        tournament.setStatus(tournamentStatus);
        return tournament;
    }

    private Configuration createGroupGameBestOfConfiguration(ConfigurationKey configurationKey) {
        final Configuration configuration = new Configuration();
        configuration.setKey(configurationKey);
        configuration.setValue("3");
        return configuration;
    }
}
