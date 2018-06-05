package com.cwtsite.cwt.domain.group.service;

import com.cwtsite.cwt.entity.GroupStanding;
import com.cwtsite.cwt.domain.game.entity.Game;
import com.cwtsite.cwt.domain.group.entity.Group;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupStandingRepository groupStandingRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, GroupStandingRepository groupStandingRepository) {
        this.groupRepository = groupRepository;
        this.groupStandingRepository = groupStandingRepository;
    }

    public List<Group> save(List<Group> groups) {
        return groupRepository.save(groups);
    }

    public List<Group> getGroupsForTournament(final Tournament tournament) {
        return groupRepository.findByTournament(tournament);
    }

    public void calcTableByGame(final Game game, final boolean homeUserHasWon) {
        final GroupStanding standingOfHomeUser = game.getGroup().getStandings().stream()
                .filter(s -> s.getUser().equals(game.getHomeUser()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        final GroupStanding standingOfAwayUser = game.getGroup().getStandings().stream()
                .filter(s -> s.getUser().equals(game.getAwayUser()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        // TODO Points pattern from configurations.

        GroupStanding standingOfWinner;
        GroupStanding standingOfLoser;

        if (homeUserHasWon) {
            standingOfWinner = standingOfHomeUser;
            standingOfLoser = standingOfAwayUser;

            if (game.getScoreAway() == 2) {
                standingOfLoser.setPoints(standingOfLoser.getPoints() + 1);
            }

            standingOfWinner.setRoundRatio(standingOfWinner.getRoundRatio() + (game.getScoreHome() - game.getScoreAway()));
            standingOfLoser.setRoundRatio(standingOfLoser.getRoundRatio() + (game.getScoreAway() - game.getScoreHome()));
        } else {
            standingOfWinner = standingOfAwayUser;
            standingOfLoser = standingOfHomeUser;

            if (game.getScoreHome() == 2) {
                standingOfLoser.setPoints(standingOfLoser.getPoints() + 1);
            }

            standingOfWinner.setRoundRatio(standingOfWinner.getRoundRatio() + (game.getScoreAway() - game.getScoreHome()));
            standingOfLoser.setRoundRatio(standingOfLoser.getRoundRatio() + (game.getScoreHome() - game.getScoreAway()));
        }

        standingOfWinner.setPoints(standingOfWinner.getPoints() + 3);

        standingOfWinner.setGameRatio(standingOfWinner.getGameRatio() + 1);
        standingOfLoser.setGameRatio(standingOfLoser.getGameRatio() - 1);

        standingOfWinner.setGames(standingOfHomeUser.getGames() + 1);
        standingOfLoser.setGames(standingOfAwayUser.getGames() + 1);

        groupStandingRepository.save(Arrays.asList(standingOfWinner, standingOfLoser));
    }
}
