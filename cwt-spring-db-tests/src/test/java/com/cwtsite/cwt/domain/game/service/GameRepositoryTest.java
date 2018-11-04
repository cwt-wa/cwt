package com.cwtsite.cwt.domain.game.service;

import com.btc.redg.generated.*;
import com.cwtsite.cwt.domain.game.entity.Game;
import com.cwtsite.cwt.domain.group.entity.Group;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import test.AbstractDbTest;

import java.util.Arrays;
import java.util.Objects;

public class GameRepositoryTest extends AbstractDbTest {

    @Autowired
    private GameRepository gameRepository;

    @Test
    public void findPlayedByUserInGroup() {
        final RedG redG = createRedG();

        final GTournament gTournament = redG.addTournament()
                .status(TournamentStatus.GROUP.name());

        final GGroup gGroup = redG.addGroup();
        final GUser gUser = redG.addUser();

        // The game to find (as away user).
        final GGame expectedGame1 = redG.addGame()
                .awayUserIdUser(gUser)
                .homeUserIdUser(redG.dummyUser())
                .reporterIdUser(redG.dummyUser())
                .groupIdGroup(gGroup);

        // The game to find (as home user).
        final GGame expectedGame2 = redG.addGame()
                .awayUserIdUser(redG.dummyUser())
                .homeUserIdUser(gUser)
                .reporterIdUser(redG.dummyUser())
                .groupIdGroup(gGroup);

        // Not yet reported.
        redG.addGame()
                .awayUserIdUser(gUser)
                .homeUserIdUser(redG.dummyUser())
                .reporterIdUser(null)
                .groupIdGroup(gGroup);

        // Other group.
        redG.addGame()
                .awayUserIdUser(gUser)
                .homeUserIdUser(redG.dummyUser())
                .reporterIdUser(redG.dummyUser())
                .groupIdGroup(redG.dummyGroup());

        // Opponent is null.
        redG.addGame()
                .awayUserIdUser(gUser)
                .homeUserIdUser(null)
                .reporterIdUser(redG.dummyUser())
                .groupIdGroup(gGroup);

        // User not included.
        redG.addGame()
                .awayUserIdUser(redG.dummyUser())
                .homeUserIdUser(redG.dummyUser())
                .reporterIdUser(redG.dummyUser())
                .groupIdGroup(gGroup);

        redG.insertDataIntoDatabase(dataSource);

        final Tournament tournament = em.find(Tournament.class, gTournament.id());
        final Group group = em.find(Group.class, gGroup.id());

        final User user = loadUsersGetOneWorkaround(gUser.id());

        Assert.assertEquals(
                gameRepository.findAllById(Arrays.asList(expectedGame1.id(), expectedGame2.id())),
                gameRepository.findPlayedByUserInGroup(user, group));
    }

    @Test
    public void findNextPlayoffGameForUser() {
        final RedG redG = createRedG();

        final GUser gUser = redG.addUser();

        final GTournament gTournament = redG.addTournament()
                .status(TournamentStatus.PLAYOFFS.name());

        createPlayoffGame(redG, gTournament, 1, 1);
        createPlayoffGame(redG, gTournament, 1, 2);
        createPlayoffGame(redG, gTournament, 1, 4);
        createPlayoffGame(redG, gTournament, 2, 1);

        final GGame gGame13 = createPlayoffGame(redG, gTournament, 1, 3);
        gGame13.homeUserIdUser(gUser);

        final GGame gGame22 = createPlayoffGame(redG, gTournament, 2, 2);
        gGame22.homeUserIdUser(gUser);
        gGame22.reporterIdUser(null);

        redG.insertDataIntoDatabase(dataSource);

        final Tournament tournament = em.find(Tournament.class, gTournament.id());
        final User user = loadUsersGetOneWorkaround(gUser.id());

        Assert.assertEquals(
                em.find(Game.class, gGame22.id()),
                gameRepository.findNextPlayoffGameForUser(tournament, user));
    }

    private GGame createPlayoffGame(RedG redG, GTournament tournament, int round, int spot) {
        final GPlayoffGame gPlayoffGame = redG.addPlayoffGame()
                .round(round)
                .spot(spot);

        return redG.addGame()
                .playoffIdPlayoffGame(gPlayoffGame)
                .groupIdGroup(null)
                .homeUserIdUser(redG.dummyUser())
                .awayUserIdUser(redG.dummyUser())
                .reporterIdUser(redG.dummyUser())
                .tournamentIdTournament(tournament);
    }

    private User loadUsersGetOneWorkaround(Long userId) {
        // em.find(User.class, gUser.id()) yields null for some reason...
        return em.getEntityManager().createQuery("select u from User u", User.class).getResultList().stream()
                .filter(u -> Objects.equals(u.getId(), userId))
                .findFirst().orElseThrow(RuntimeException::new);
    }

    @Test
    public void findGameInPlayoffTree() {
        final RedG redG = createRedG();

        final GUser gUser = redG.addUser();

        final GTournament gTournament = redG.addTournament()
                .status(TournamentStatus.PLAYOFFS.name());

        createPlayoffGame(redG, gTournament, 1, 1);
        createPlayoffGame(redG, gTournament, 1, 2);
        final GGame gameJustPlayed = createPlayoffGame(redG, gTournament, 1, 3);
        createPlayoffGame(redG, gTournament, 1, 4);
        createPlayoffGame(redG, gTournament, 2, 1);
        final GGame gameToAdvanceTo = createPlayoffGame(redG, gTournament, 2, 2);

        gameJustPlayed
                .homeUserIdUser(gUser)
                .scoreHome(3)
                .scoreAway(1);

        gameToAdvanceTo
                .homeUserIdUser(null)
                .id(99L);

        redG.insertDataIntoDatabase(dataSource);
        loadUsersGetOneWorkaround(gUser.id());

        //noinspection OptionalGetWithoutIsPresent
        Assert.assertEquals(
                gameRepository.findGameInPlayoffTree(em.find(Tournament.class, gTournament.id()), 2, 2).get().getId(),
                gameToAdvanceTo.id());
    }
}
