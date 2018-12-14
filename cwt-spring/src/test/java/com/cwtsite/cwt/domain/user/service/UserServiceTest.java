package com.cwtsite.cwt.domain.user.service;

import com.cwtsite.cwt.domain.game.entity.Game;
import com.cwtsite.cwt.domain.game.service.GameRepository;
import com.cwtsite.cwt.domain.group.entity.Group;
import com.cwtsite.cwt.domain.group.service.GroupRepository;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus;
import com.cwtsite.cwt.domain.tournament.service.TournamentService;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import com.cwtsite.cwt.entity.GroupStanding;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private TournamentService tournamentService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GameRepository gameRepository;


    @Test
    public void getRemainingOpponents() {
        final User user = createUser(1);
        final Tournament tournament = new Tournament();
        tournament.setStatus(TournamentStatus.GROUP);

        Mockito
                .when(tournamentService.getCurrentTournament())
                .thenReturn(tournament);

        final Group group = new Group();
        group.setStandings(createStandings(group, user));

        Mockito
                .when(groupRepository.findByTournamentAndUser(Mockito.any(), Mockito.any()))
                .thenReturn(group);
        Mockito
                .when(gameRepository.findPlayedByUserInGroup(Mockito.any(), Mockito.any()))
                .thenReturn(createGames(group));

        Assertions
                .assertThat(userService.getRemainingOpponents(user))
                .containsExactlyInAnyOrder(getUser(group, 3), getUser(group, 4));
    }

    private List<Game> createGames(Group group) {
        final Game game1 = new Game();
        game1.setGroup(group);
        game1.setId(LocalTime.now().toNanoOfDay());
        game1.setHomeUser(getUser(group, 1));
        game1.setAwayUser(getUser(group, 2));
        return Collections.singletonList(game1);
    }

    private User getUser(Group group, int userId) {
        return group.getStandings().stream()
                .map(GroupStanding::getUser)
                .filter(u -> u.getId() == userId).findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private List<GroupStanding> createStandings(Group group, User user) {
        return Arrays.asList(
                new GroupStanding(group, user),
                new GroupStanding(group, createUser(2)),
                new GroupStanding(group, createUser(3)),
                new GroupStanding(group, createUser(4))
        );
    }

    private User createUser(long id) {
        final User user = new User();
        user.setId(id);
        return user;
    }

    @Test
    public void validateUsername() {
        Assert.assertFalse(userService.validateUsername("Strawberrycheesecake"));
        Assert.assertFalse(userService.validateUsername("Zemke`NNN"));
        Assert.assertFalse(userService.validateUsername("Zemke "));
        Assert.assertFalse(userService.validateUsername(" "));
        Assert.assertFalse(userService.validateUsername(""));
        Assert.assertTrue(userService.validateUsername("Z3mk3"));
        Assert.assertTrue(userService.validateUsername("Zemke"));
    }

    @Test
    public void validateEmail() {
        Assert.assertFalse(userService.validateEmail("@"));
        Assert.assertFalse(userService.validateEmail("asdsad"));
        Assert.assertFalse(userService.validateEmail("asdsad@"));
        Assert.assertFalse(userService.validateEmail("@asds ad"));
        Assert.assertFalse(userService.validateEmail("sad @cho"));
        Assert.assertFalse(userService.validateEmail("sad@cho "));
        Assert.assertFalse(userService.validateEmail(" sad@cho"));
        Assert.assertFalse(userService.validateEmail(" sad@cho"));
        Assert.assertTrue(userService.validateEmail("sad@cho"));
        Assert.assertTrue(userService.validateEmail("choc@choch.de"));
        Assert.assertTrue(userService.validateEmail("choc@choch.com"));
    }
}
