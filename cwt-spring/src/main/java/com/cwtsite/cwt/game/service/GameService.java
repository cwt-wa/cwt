package com.cwtsite.cwt.game.service;

import com.cwtsite.cwt.game.entity.Game;
import com.cwtsite.cwt.game.view.model.ReportDto;
import com.cwtsite.cwt.group.entity.Group;
import com.cwtsite.cwt.group.service.GroupRepository;
import com.cwtsite.cwt.group.service.GroupService;
import com.cwtsite.cwt.tournament.entity.Tournament;
import com.cwtsite.cwt.tournament.service.TournamentService;
import com.cwtsite.cwt.user.repository.UserRepository;
import com.cwtsite.cwt.user.repository.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class GameService {

    private final GameRepository gameRepository;
    private final TournamentService tournamentService;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupService groupService;

    @Autowired
    public GameService(GameRepository gameRepository, TournamentService tournamentService, GroupRepository groupRepository,
                       UserRepository userRepository, GroupService groupService) {
        this.gameRepository = gameRepository;
        this.tournamentService = tournamentService;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupService = groupService;
    }

    @Transactional
    public Game reportGame(final ReportDto reportDto) {
        final Game game = map(reportDto);
        groupService.calcTableByGame(game, homeUserHasWon(game));
        return gameRepository.save(game);
    }

    private Game map(final ReportDto reportDto) {
        final Tournament currentTournament = tournamentService.getCurrentTournament();
        final Game game = new Game();

        // TODO Three queries in a row -- performance?
        final User reportingUser = userRepository.findOne(reportDto.getUser());
        final User opponent = userRepository.findOne(reportDto.getOpponent());
        final Group group = groupRepository.findByTournamentAndUser(currentTournament, opponent);

        game.setScoreHome(Math.toIntExact(reportDto.getScoreOfUser()));
        game.setScoreAway(Math.toIntExact(reportDto.getScoreOfOpponent()));
        game.setTournament(currentTournament);
        game.setGroup(group);
        game.setHomeUser(reportingUser);
        game.setAwayUser(opponent);
        game.setReporter(reportingUser);

        return game;
    }

    public boolean homeUserHasWon(final Game game) {
        return game.getScoreHome() > game.getScoreAway();
    }

    public List<Game> saveAll(final List<Game> games) {
        return gameRepository.save(games);
    }

    public Game get(long id) {
        return gameRepository.findOne(id);
    }
}
