package com.cwtsite.cwt.domain.playoffs.service;

import com.cwtsite.cwt.domain.game.entity.Game;
import com.cwtsite.cwt.domain.game.entity.PlayoffGame;
import com.cwtsite.cwt.domain.game.service.GameRepository;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@RunWith(MockitoJUnitRunner.class)
public class PlayoffServiceTest {

    @InjectMocks
    private PlayoffService playoffService;

    @Mock
    private GameRepository gameRepository;

    @Test
    public void advanceByGame_playoffGameExists() {
        final Game game = createGame(1L, createUser(1L), createUser(2L), 3, 0, createPlayoffGame(1, 3), createTournament());
        final User upcomingAwayUser = createUser(3L);

        Mockito
                .when(gameRepository.findGameInPlayoffTree(game.getTournament(), 2, 2))
                .thenReturn(Optional.of(
                        createGame(2L, null, upcomingAwayUser, null, null, createPlayoffGame(2, 2), createTournament())));

        Mockito
                .when(gameRepository.save(Mockito.any()))
                .thenAnswer(invocation -> {
                    final Game actualGame = invocation.getArgument(0);

                    Assert.assertEquals(2, (int) actualGame.getPlayoff().getRound());
                    Assert.assertEquals(2, (int) actualGame.getPlayoff().getSpot());
                    Assert.assertEquals(game.getTournament(), actualGame.getTournament());
                    Assert.assertEquals(game.getHomeUser(), actualGame.getHomeUser());
                    Assert.assertEquals(upcomingAwayUser, actualGame.getAwayUser());
                    Assert.assertEquals(2, (long) actualGame.getId());
                    Assert.assertNull(actualGame.getGroup());

                    return actualGame;
                });

        playoffService.advanceByGame(game);
    }

    @Test
    public void advanceByGame_playoffGameDoesNotExist() {
        final Game game = createGame(1L, createUser(1L), createUser(2L), 2, 3, createPlayoffGame(1, 2), createTournament());

        Mockito
                .when(gameRepository.findGameInPlayoffTree(game.getTournament(), 1, 2))
                .thenReturn(Optional.empty());

        Mockito
                .when(gameRepository.save(Mockito.any()))
                .thenAnswer(invocation -> {
                    final Game actualGame = invocation.getArgument(0);

                    Assert.assertEquals(2, (int) actualGame.getPlayoff().getRound());
                    Assert.assertEquals(1, (int) actualGame.getPlayoff().getSpot());
                    Assert.assertEquals(game.getTournament(), actualGame.getTournament());
                    Assert.assertEquals(game.getAwayUser(), actualGame.getAwayUser());
                    Assert.assertNull(actualGame.getHomeUser());
                    Assert.assertNull(actualGame.getId());
                    Assert.assertNull(actualGame.getGroup());

                    return actualGame;
                });

        playoffService.advanceByGame(game);
    }

    @Test
    public void advanceByGame_roundSpotCalc() {
        final Tournament tournament = createTournament();
        final long gameId = 1L;
        final User homeUser = createUser(gameId);
        final User awayUser = createUser(2L);

        Mockito
                .when(gameRepository.findGameInPlayoffTree(Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenAnswer(invocation -> assertRoundSpot(invocation, 2, 3)) // Coming from round 1 and spot 6.
                .thenAnswer(invocation -> assertRoundSpot(invocation, 4, 1)) // Coming from round 3 and spot 1.
                .thenAnswer(invocation -> assertRoundSpot(invocation, 3, 1)) // Coming from round 2 and spot 2.
                .thenAnswer(invocation -> assertRoundSpot(invocation, 3, 2)); // Coming from round 2 and spot 4.

        playoffService.advanceByGame(createGame(gameId, homeUser, awayUser, 2, 3, createPlayoffGame(1, 6), tournament));
        playoffService.advanceByGame(createGame(gameId, homeUser, awayUser, 2, 3, createPlayoffGame(3, 1), tournament));
        playoffService.advanceByGame(createGame(gameId, homeUser, awayUser, 2, 3, createPlayoffGame(2, 2), tournament));
        playoffService.advanceByGame(createGame(gameId, homeUser, awayUser, 2, 3, createPlayoffGame(2, 4), tournament));
    }

    private Object assertRoundSpot(InvocationOnMock invocation, int expectedRound, int expectedSpot) {
        int actualSpot = invocation.getArgument(1);
        int actualRound = invocation.getArgument(2);

        Assert.assertEquals(expectedRound, actualRound);
        Assert.assertEquals(expectedSpot, actualSpot);

        return Optional.of(new Game());
    }

    @Test
    public void advanceByGame_advanceAsHomeOrAway() {
        final Tournament tournament = createTournament();
        final long gameId = 1L;
        final User homeUser = createUser(gameId);
        final User awayUser = createUser(2L);

        Mockito
                .when(gameRepository.findGameInPlayoffTree(Mockito.any(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(Optional.empty());

        final BiFunction<InvocationOnMock, User, Game> doAssertion = (InvocationOnMock invocation, User whoIsNull) -> {
            final Game actualGame = invocation.getArgument(0);
            Assert.assertNull(whoIsNull);
            return actualGame;
        };

        Mockito
                .when(gameRepository.save(Mockito.any()))
                .thenAnswer(invocation -> {
                    Assert.assertEquals(((Game) invocation.getArgument(0)).getHomeUser(), homeUser);
                    Assert.assertNull(((Game) invocation.getArgument(0)).getAwayUser());
                    return invocation.getArgument(0);
                }) // winner home, round 1, spot 3
                .thenAnswer(invocation -> {
                    Assert.assertNull(((Game) invocation.getArgument(0)).getHomeUser());
                    Assert.assertEquals(((Game) invocation.getArgument(0)).getAwayUser(), awayUser);
                    return invocation.getArgument(0);
                }) // winner away, round 2, spot 4
                .thenAnswer(invocation -> {
                    Assert.assertEquals(((Game) invocation.getArgument(0)).getHomeUser(), homeUser);
                    Assert.assertNull(((Game) invocation.getArgument(0)).getAwayUser());
                    return invocation.getArgument(0);
                }) // winner home, round 3, spot 1
                .thenAnswer(invocation -> {
                    Assert.assertNull(((Game) invocation.getArgument(0)).getHomeUser());
                    Assert.assertEquals(((Game) invocation.getArgument(0)).getAwayUser(), awayUser);
                    return invocation.getArgument(0);
                }) // winner away, round 1, spot 8
                .thenAnswer(invocation -> {
                    Assert.assertEquals(((Game) invocation.getArgument(0)).getHomeUser(), awayUser);
                    Assert.assertNull(((Game) invocation.getArgument(0)).getAwayUser());
                    return invocation.getArgument(0);
                }); // winner away, round 2, spot 1

        playoffService.advanceByGame(createGame(gameId, homeUser, awayUser, 3, 1, createPlayoffGame(1, 3), tournament));
        playoffService.advanceByGame(createGame(gameId, homeUser, awayUser, 2, 3, createPlayoffGame(2, 4), tournament));
        playoffService.advanceByGame(createGame(gameId, homeUser, awayUser, 3, 1, createPlayoffGame(3, 1), tournament));
        playoffService.advanceByGame(createGame(gameId, homeUser, awayUser, 2, 3, createPlayoffGame(1, 8), tournament));
        playoffService.advanceByGame(createGame(gameId, homeUser, awayUser, 0, 3, createPlayoffGame(2, 1), tournament));
    }

    private Game createGame(Long id,
                            User homeUser, User awayUser,
                            Integer scoreHome, Integer scoreAway,
                            PlayoffGame playoffGame, Tournament tournament) {
        final Game game = new Game();
        game.setId(id);
        game.setHomeUser(homeUser);
        game.setAwayUser(awayUser);
        game.setScoreHome(scoreHome);
        game.setScoreAway(scoreAway);
        game.setPlayoff(playoffGame);
        game.setTournament(tournament);
        return game;
    }

    private Tournament createTournament() {
        final Tournament tournament = new Tournament();
        tournament.setId(1L);
        return tournament;
    }

    private PlayoffGame createPlayoffGame(int round, int spot) {
        final PlayoffGame playoffGame = new PlayoffGame();
        playoffGame.setRound(round);
        playoffGame.setSpot(spot);
        return playoffGame;
    }

    private User createUser(Long id) {
        final User user = new User();
        user.setId(id);
        return user;
    }
}

