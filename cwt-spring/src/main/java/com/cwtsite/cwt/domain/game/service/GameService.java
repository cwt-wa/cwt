package com.cwtsite.cwt.domain.game.service;

import com.cwtsite.cwt.core.FileValidator;
import com.cwtsite.cwt.domain.configuration.entity.Configuration;
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey;
import com.cwtsite.cwt.domain.configuration.service.ConfigurationService;
import com.cwtsite.cwt.domain.game.entity.Game;
import com.cwtsite.cwt.domain.game.entity.Rating;
import com.cwtsite.cwt.domain.game.entity.Replay;
import com.cwtsite.cwt.domain.game.entity.enumeration.RatingType;
import com.cwtsite.cwt.domain.group.entity.Group;
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
import java.util.stream.Collectors;

@Component
public class GameService {

    private final GameRepository gameRepository;
    private final RatingRepository ratingRepository;
    private final TournamentService tournamentService;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupService groupService;
    private final CommentRepository commentRepository;
    private final ConfigurationService configurationService;
    private final UserService userService;
    private final PlayoffService playoffService;

    @Autowired
    public GameService(GameRepository gameRepository, TournamentService tournamentService, GroupRepository groupRepository,
                       UserRepository userRepository, GroupService groupService, RatingRepository ratingRepository,
                       CommentRepository commentRepository, ConfigurationService configurationService, UserService userService,
                       PlayoffService playoffService) {
        this.gameRepository = gameRepository;
        this.tournamentService = tournamentService;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.groupService = groupService;
        this.ratingRepository = ratingRepository;
        this.commentRepository = commentRepository;
        this.configurationService = configurationService;
        this.userService = userService;
        this.playoffService = playoffService;
    }

    @Transactional
    public Game reportGame(long homeUser, long awayUser, int scoreHome, int scoreAway, MultipartFile replay)
            throws InvalidOpponentException, InvalidScoreException, IllegalTournamentStatusException,
            FileValidator.UploadSecurityException, FileValidator.IllegalFileContentTypeException,
            FileValidator.FileEmptyException, FileValidator.FileTooLargeException, FileValidator.IllegalFileExtension,
            IOException {
        FileValidator.validate(replay, 150000, Arrays.asList("application/x-rar", "application/zip"), Arrays.asList("rar", "zip"));
        final Game reportedGame = reportGame(homeUser, awayUser, scoreHome, scoreAway);
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
    public Game reportGame(long homeUserId, long awayUserId, int homeScore, int awayScore)
            throws InvalidOpponentException, InvalidScoreException, IllegalTournamentStatusException {
        final Tournament currentTournament = tournamentService.getCurrentTournament();
        final Configuration bestOfValue = getBestOfValue(currentTournament.getStatus());

        if (homeScore + awayScore != Integer.valueOf(bestOfValue.getValue())) {
            throw new InvalidScoreException(String.format(
                    "Score %s-%s should have been best of %s.",
                    homeScore, awayScore, bestOfValue.getValue()));
        } else if (homeScore < 0 || awayScore < 0) {
            throw new InvalidScoreException(String.format(
                    "Score %s-%s should not include negative scores.", homeScore, awayScore));
        }

        final User homeUser = userRepository.findById(homeUserId).orElseThrow(RuntimeException::new);
        final List<User> remainingOpponents = userService.getRemainingOpponents(homeUser);
        final User awayUser = userRepository.findById(awayUserId).orElseThrow(RuntimeException::new);

        if (!remainingOpponents.contains(awayUser)) {
            throw new InvalidOpponentException(String.format(
                    "Opponent %s is not in %s",
                    awayUser.getId(), remainingOpponents.stream().map(User::getId).collect(Collectors.toList())));
        }

        Game reportedGame;

        if (currentTournament.getStatus() == TournamentStatus.GROUP) {
            final Group group = groupRepository.findByTournamentAndUser(currentTournament, awayUser);

            final Game game = new Game();

            game.setScoreHome(homeScore);
            game.setScoreAway(awayScore);
            game.setTournament(currentTournament);
            game.setHomeUser(homeUser);
            game.setAwayUser(awayUser);
            game.setReporter(homeUser);

            game.setGroup(group);

            groupService.calcTableByGame(game);
            reportedGame = gameRepository.save(game);
        } else if (currentTournament.getStatus() == TournamentStatus.PLAYOFFS) {
            final Game playoffGameToBeReported =
                    gameRepository.findNextPlayoffGameForUser(currentTournament, homeUser);

            if (!Arrays.asList(playoffGameToBeReported.getHomeUser(), playoffGameToBeReported.getAwayUser())
                    .containsAll(Arrays.asList(homeUser, awayUser))) {
                throw new InvalidOpponentException(String.format(
                        "Next playoff game is expected to be %s vs. %s.",
                        homeUser.getUsername(), awayUser.getUsername()));
            }

            playoffGameToBeReported.setReporter(homeUser);

            if (playoffGameToBeReported.getHomeUser().equals(homeUser)) {
                playoffGameToBeReported.setScoreHome(homeScore);
                playoffGameToBeReported.setScoreAway(awayScore);
            } else {
                playoffGameToBeReported.setScoreHome(awayScore);
                playoffGameToBeReported.setScoreAway(homeScore);
            }

            reportedGame = gameRepository.save(playoffGameToBeReported);
            playoffService.advanceByGame(reportedGame);
        } else {
            throw new IllegalTournamentStatusException(TournamentStatus.GROUP, TournamentStatus.PLAYOFFS);
        }

        return reportedGame;
    }

    public Configuration getBestOfValue(TournamentStatus tournamentStatus) {
        ConfigurationKey configurationKey;

        if (tournamentStatus == TournamentStatus.GROUP) {
            configurationKey = ConfigurationKey.GROUP_GAMES_BEST_OF;
        } else if (tournamentStatus == TournamentStatus.PLAYOFFS) {
            configurationKey = playoffService.onlyFinalGamesAreLeftToPlay()
                    ? ConfigurationKey.FINAL_GAME_BEST_OF
                    : ConfigurationKey.PLAYOFF_GAMES_BEST_OF;
        } else {
            throw new IllegalTournamentStatusException(TournamentStatus.GROUP, TournamentStatus.PLAYOFFS);
        }

        return configurationService.getOne(configurationKey);
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

    public class InvalidScoreException extends RuntimeException {
        InvalidScoreException(String message) {
            super(message);
        }
    }

    public class InvalidOpponentException extends RuntimeException {
        InvalidOpponentException(String message) {
            super(message);
        }
    }
}
