package com.cwtsite.cwt.domain.game.service;

import com.cwtsite.cwt.core.FileValidator;
import com.cwtsite.cwt.domain.game.entity.Game;
import com.cwtsite.cwt.domain.game.entity.Rating;
import com.cwtsite.cwt.domain.game.entity.Replay;
import com.cwtsite.cwt.domain.game.entity.enumeration.RatingType;
import com.cwtsite.cwt.domain.group.entity.Group;
import com.cwtsite.cwt.domain.group.service.GroupRepository;
import com.cwtsite.cwt.domain.group.service.GroupService;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus;
import com.cwtsite.cwt.domain.tournament.exception.IllegalTournamentStatusException;
import com.cwtsite.cwt.domain.tournament.service.TournamentService;
import com.cwtsite.cwt.domain.user.repository.UserRepository;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import com.cwtsite.cwt.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
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
    public Game reportGame(long homeUser, long awayUser, int scoreHome, int scoreAway, MultipartFile replay) throws IOException {
        final Game reportedGame = reportGame(homeUser, awayUser, scoreHome, scoreAway);
        FileValidator.validate(replay, 150000, Arrays.asList("application/x-rar", "application/zip"), Arrays.asList("rar", "zip"));
        reportedGame.setReplay(new Replay(replay.getBytes(), replay.getContentType(), StringUtils.getFilenameExtension(replay.getOriginalFilename())));
        return gameRepository.save(reportedGame);
    }

    public String createReplayFileName(Game game) {
        return String.format(
                "%s_%s_%s-%s_%s.%s",
                game.getId(),
                game.getHomeUser().getUsername().replaceAll("[^a-zA-Z0-9-_\\\\.]", "_"),
                game.getScoreHome(), game.getScoreAway(),
                game.getAwayUser().getUsername().replaceAll("[^a-zA-Z0-9-_\\\\.]", "_"),
                game.getReplay().getExtension());
    }

    @Transactional
    public Game reportGame(long homeUser, long awayUser, int homeScore, int awayScore) {
        final Tournament currentTournament = tournamentService.getCurrentTournament();
        final User reportingUser = userRepository.findById(homeUser)
                .orElseThrow(IllegalArgumentException::new);

        Game reportedGame;

        if (currentTournament.getStatus() == TournamentStatus.GROUP) {
            final User opponent = userRepository.findById(awayUser)
                    .orElseThrow(IllegalArgumentException::new);
            final Group group = groupRepository.findByTournamentAndUser(currentTournament, opponent);

            final Game game = new Game();

            game.setScoreHome(homeScore);
            game.setScoreAway(awayScore);
            game.setTournament(currentTournament);
            game.setHomeUser(reportingUser);
            game.setAwayUser(opponent);
            game.setReporter(reportingUser);

            game.setGroup(group);


            groupService.calcTableByGame(game, homeUserHasWon(game));
            reportedGame = gameRepository.save(game);
        } else if (currentTournament.getStatus() == TournamentStatus.PLAYOFFS) {
            // TODO Advance the winner.

            final Game playoffGameToBeReported =
                    gameRepository.findNextPlayoffGameForUser(currentTournament, reportingUser);
            playoffGameToBeReported.setReporter(reportingUser);

            if (playoffGameToBeReported.getHomeUser().equals(reportingUser)) {
                playoffGameToBeReported.setScoreHome(Math.toIntExact(homeScore));
                playoffGameToBeReported.setScoreAway(Math.toIntExact(awayScore));
            } else {
                playoffGameToBeReported.setScoreHome(Math.toIntExact(awayScore));
                playoffGameToBeReported.setScoreAway(Math.toIntExact(homeScore));
            }

            reportedGame = gameRepository.save(playoffGameToBeReported);
        } else {
            throw new IllegalTournamentStatusException(TournamentStatus.GROUP, TournamentStatus.PLAYOFFS);
        }

        return reportedGame;
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
