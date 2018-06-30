package com.cwtsite.cwt.domain.game.service;

import com.cwtsite.cwt.entity.Comment;
import com.cwtsite.cwt.domain.game.entity.Game;
import com.cwtsite.cwt.domain.game.entity.Rating;
import com.cwtsite.cwt.domain.game.entity.enumeration.RatingType;
import com.cwtsite.cwt.domain.game.view.model.ReportDto;
import com.cwtsite.cwt.domain.group.entity.Group;
import com.cwtsite.cwt.domain.group.service.GroupRepository;
import com.cwtsite.cwt.domain.group.service.GroupService;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus;
import com.cwtsite.cwt.domain.tournament.exception.IllegalTournamentStatusException;
import com.cwtsite.cwt.domain.tournament.service.TournamentService;
import com.cwtsite.cwt.domain.user.repository.UserRepository;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class GameService {

    private final GameRepository gameRepository;
    private final RatingRepository ratingRepository;
    private final TournamentService tournamentService;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupService groupService;
    private final CommentRepository commentRepository;

    @Autowired
    public GameService(GameRepository gameRepository, TournamentService tournamentService, GroupRepository groupRepository,
                       UserRepository userRepository, GroupService groupService, RatingRepository ratingRepository,
                       CommentRepository commentRepository) {
        this.gameRepository = gameRepository;
        this.tournamentService = tournamentService;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupService = groupService;
        this.ratingRepository = ratingRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public Game reportGame(final ReportDto reportDto) {
        final Tournament currentTournament = tournamentService.getCurrentTournament();
        final User reportingUser = userRepository.findById(reportDto.getUser())
                .orElseThrow(IllegalArgumentException::new);

        Game reportedGame;

        if (currentTournament.getStatus() == TournamentStatus.GROUP) {
            final User opponent = userRepository.findById(reportDto.getOpponent())
                    .orElseThrow(IllegalArgumentException::new);
            final Group group = groupRepository.findByTournamentAndUser(currentTournament, opponent);

            Game mappedGame = ReportDto.map(reportDto, currentTournament, reportingUser, opponent, group);
            groupService.calcTableByGame(mappedGame, homeUserHasWon(mappedGame));
            reportedGame = gameRepository.save(mappedGame);
        } else if (currentTournament.getStatus() == TournamentStatus.PLAYOFFS) {
            // TODO Advance the winner.

            final Game playoffGameToBeReported =
                    gameRepository.findNextPlayoffGameForUser(currentTournament, reportingUser);
            playoffGameToBeReported.setReporter(reportingUser);

            if (playoffGameToBeReported.getHomeUser().equals(reportingUser)) {
                playoffGameToBeReported.setScoreHome(Math.toIntExact(reportDto.getScoreOfUser()));
                playoffGameToBeReported.setScoreAway(Math.toIntExact(reportDto.getScoreOfOpponent()));
            } else {
                playoffGameToBeReported.setScoreHome(Math.toIntExact(reportDto.getScoreOfOpponent()));
                playoffGameToBeReported.setScoreAway(Math.toIntExact(reportDto.getScoreOfUser()));
            }

            reportedGame = gameRepository.save(playoffGameToBeReported);
        } else {
            throw new IllegalTournamentStatusException(TournamentStatus.GROUP, TournamentStatus.PLAYOFFS);
        }

        return reportedGame;
    }

    private Game map(final ReportDto reportDto) {
        final Tournament currentTournament = tournamentService.getCurrentTournament();
        final Game game = new Game();

        // TODO Three queries in a row -- performance?
        final User reportingUser = userRepository.findById(reportDto.getUser())
                .orElseThrow(IllegalArgumentException::new);
        final User opponent = userRepository.findById(reportDto.getOpponent())
                .orElseThrow(IllegalArgumentException::new);
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
        return gameRepository.saveAll(games);
    }

    public Optional<Game> get(long id) {
        return gameRepository.findById(id);
    }

    public Rating rateGame(long gameId, Long userId, RatingType type) {
        final User user = userRepository.findById(userId)
                .orElseThrow(IllegalArgumentException::new);
        final Game game = gameRepository.findById(gameId)
                .orElseThrow(IllegalArgumentException::new);
        return ratingRepository.save(new Rating(type, user, game));
    }

    public Comment commentGame(long gameId, Long userId, String body) {
        final User user = userRepository.findById(userId)
                .orElseThrow(IllegalArgumentException::new);
        final Game game = gameRepository.findById(gameId)
                .orElseThrow(IllegalArgumentException::new);
        return commentRepository.save(new Comment(body, user, game));
    }
}
