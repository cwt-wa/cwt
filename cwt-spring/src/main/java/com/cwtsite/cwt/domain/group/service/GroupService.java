package com.cwtsite.cwt.domain.group.service;

import com.cwtsite.cwt.domain.configuration.service.ConfigurationService;
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus;
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository;
import com.cwtsite.cwt.entity.GroupStanding;
import com.cwtsite.cwt.domain.game.entity.Game;
import com.cwtsite.cwt.domain.group.entity.Group;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupStandingRepository groupStandingRepository;
    private final TournamentRepository tournamentRepository;
    private final ConfigurationService configurationService;

    @Autowired
    public GroupService(GroupRepository groupRepository, GroupStandingRepository groupStandingRepository,
                        TournamentRepository tournamentRepository, ConfigurationService configurationService) {
        this.groupRepository = groupRepository;
        this.groupStandingRepository = groupStandingRepository;
        this.tournamentRepository = tournamentRepository;
        this.configurationService = configurationService;
    }

    public List<Group> save(List<Group> groups) {
        return groupRepository.saveAll(groups);
    }

    public List<Group> getGroupsForTournament(final Tournament tournament) {
        return groupRepository.findByTournament(tournament);
    }

    public void calcTableByGame(final Game game) {
        final GroupStanding standingOfHomeUser = game.getGroup().getStandings().stream()
                .filter(s -> s.getUser().equals(game.getHomeUser()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        final GroupStanding standingOfAwayUser = game.getGroup().getStandings().stream()
                .filter(s -> s.getUser().equals(game.getAwayUser()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);

        final List<int[]> pointsPattern = configurationService.getParsedPointsPatternConfiguration();

        GroupStanding standingOfWinner;
        GroupStanding standingOfLoser;

        if (game.getScoreHome() > game.getScoreAway()) {
            standingOfWinner = standingOfHomeUser;
            standingOfLoser = standingOfAwayUser;

            standingOfWinner.setPoints(standingOfWinner.getPoints() + getPointsForScore(pointsPattern, game.getScoreHome()));
            standingOfLoser.setPoints(standingOfLoser.getPoints() + getPointsForScore(pointsPattern, game.getScoreAway()));

            standingOfWinner.setRoundRatio(standingOfWinner.getRoundRatio() + (game.getScoreHome() - game.getScoreAway()));
            standingOfLoser.setRoundRatio(standingOfLoser.getRoundRatio() + (game.getScoreAway() - game.getScoreHome()));
        } else {
            standingOfWinner = standingOfAwayUser;
            standingOfLoser = standingOfHomeUser;

            standingOfWinner.setPoints(standingOfWinner.getPoints() + getPointsForScore(pointsPattern, game.getScoreAway()));
            standingOfLoser.setPoints(standingOfLoser.getPoints() + getPointsForScore(pointsPattern, game.getScoreHome()));

            standingOfWinner.setRoundRatio(standingOfWinner.getRoundRatio() + (game.getScoreAway() - game.getScoreHome()));
            standingOfLoser.setRoundRatio(standingOfLoser.getRoundRatio() + (game.getScoreHome() - game.getScoreAway()));
        }

        standingOfWinner.setGameRatio(standingOfWinner.getGameRatio() + 1);
        standingOfLoser.setGameRatio(standingOfLoser.getGameRatio() - 1);

        standingOfWinner.setGames(standingOfHomeUser.getGames() + 1);
        standingOfLoser.setGames(standingOfAwayUser.getGames() + 1);

        groupStandingRepository.saveAll(Arrays.asList(standingOfWinner, standingOfLoser));
    }

    private int getPointsForScore(List<int[]> pointsPattern, Integer score) {
        return pointsPattern.stream()
                .filter(pp -> pp[0] == score)
                .map(pp -> pp[1])
                .findFirst().orElseThrow(RuntimeException::new);
    }

    @Transactional
    public List<Group> startGroupStage(Tournament tournament, List<Group> groups) {
        tournament.setStatus(TournamentStatus.GROUP);
        tournamentRepository.save(tournament);
        return groupRepository.saveAll(groups);
    }
}
