package com.cwtsite.cwt.domain.user.service;

import com.cwtsite.cwt.domain.application.service.ApplicationRepository;
import com.cwtsite.cwt.domain.game.entity.Game;
import com.cwtsite.cwt.domain.game.service.GameRepository;
import com.cwtsite.cwt.domain.group.entity.Group;
import com.cwtsite.cwt.domain.group.service.GroupRepository;
import com.cwtsite.cwt.domain.playoffs.service.PlayoffService;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus;
import com.cwtsite.cwt.domain.tournament.service.TournamentService;
import com.cwtsite.cwt.domain.user.repository.UserRepository;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import com.cwtsite.cwt.entity.GroupStanding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class UserService {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final TournamentService tournamentService;
    private final ApplicationRepository applicationRepository;
    private final GroupRepository groupRepository;
    private final PlayoffService playoffService;
    private final GameRepository gameRepository;

    @Autowired
    public UserService(UserRepository userRepository, AuthService authService, TournamentService tournamentService,
                       ApplicationRepository applicationRepository, GroupRepository groupRepository,
                       PlayoffService playoffService, GameRepository gameRepository) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.tournamentService = tournamentService;
        this.applicationRepository = applicationRepository;
        this.groupRepository = groupRepository;
        this.playoffService = playoffService;
        this.gameRepository = gameRepository;
    }

    @Transactional
    public User registerUser(String username, String email, String password)
            throws UserExistsByEmailOrUsernameException, InvalidUsernameException, InvalidEmailException {
        final String trimmedUsername = username.trim();
        final String trimmedEmail = email.trim();

        if (!validateUsername(trimmedUsername)) throw new InvalidUsernameException();
        if (!validateEmail(trimmedEmail)) throw new InvalidEmailException();

        if (userRepository.findByEmailEqualsOrUsernameEquals(trimmedEmail, trimmedUsername) != null) {
            throw new UserExistsByEmailOrUsernameException();
        }
        User user = new User();
        user.setUsername(trimmedUsername);
        user.setEmail(trimmedEmail);
        user.setPassword(authService.createHash(password));
        user.setActivated(true);
        return userRepository.save(user);
    }

    public boolean validateUsername(String username) {
        return username.length() <= 16 && username.matches("^[a-zA-Z0-9]+$");
    }

    public boolean validateEmail(String email) {
        return email.matches("^[^ ]+@[^ ]+$");
    }

    public Boolean userCanApplyForCurrentTournament(User user) {
        Tournament currentTournament = tournamentService.getCurrentTournament();

        return currentTournament != null && TournamentStatus.OPEN == currentTournament.getStatus()
                && applicationRepository.findByApplicantAndTournament(user, currentTournament) == null;
    }

    public List<User> getAllOrderedByUsername() {
        return userRepository.findAll(new Sort("username"));
    }

    public Optional<User> getById(long id) {
        return userRepository.findById(id);
    }

    public List<User> getByIds(List<Long> ids) {
        return userRepository.findAllById(ids);
    }

    public boolean userCanReportForCurrentTournament(final User user) {
        boolean userCanReportForCurrentTournament;
        final Tournament currentTournament = tournamentService.getCurrentTournament();

        if (currentTournament == null) {
            return false;
        }

        if (currentTournament.getStatus() == TournamentStatus.GROUP) {
            final Group group = this.groupRepository.findByTournamentAndUser(currentTournament, user);

            if (group == null) {
                userCanReportForCurrentTournament = false;
            } else {
                Integer numberOfGamesAlreadyPlayed = group.getStandings().stream()
                        .filter(s -> s.getUser().equals(user))
                        .findFirst()
                        .orElseThrow(IllegalArgumentException::new).getGames();
                int numberOfTotalGamesToPlay = group.getStandings().size() - 1;
                userCanReportForCurrentTournament = numberOfTotalGamesToPlay > numberOfGamesAlreadyPlayed;
            }
        } else if (currentTournament.getStatus() == TournamentStatus.PLAYOFFS) {
            userCanReportForCurrentTournament = playoffService.getNextGameForUser(user) != null;
        } else {
            userCanReportForCurrentTournament = false;
        }

        return userCanReportForCurrentTournament;
    }

    public List<User> getRemainingOpponents(final User user) {
        final Tournament currentTournament = tournamentService.getCurrentTournament();
        final Group group = groupRepository.findByTournamentAndUser(currentTournament, user);
        final List<Game> games = gameRepository.findPlayedByUserInGroup(user, group);
        List<User> remainingOpponents;

        if (currentTournament.getStatus() == TournamentStatus.GROUP) {
            remainingOpponents = group.getStandings().stream()
                    .filter(groupStanding -> !groupStanding.getUser().equals(user))
                    .map(GroupStanding::getUser)
                    .filter(u -> !games.stream()
                            .flatMap(g -> Stream.of(g.getHomeUser(), g.getAwayUser()))
                            .distinct()
                            .collect(Collectors.toList())
                            .contains(u))
                    .filter(u -> !u.equals(user))
                    .collect(Collectors.toList());
        } else if (currentTournament.getStatus() == TournamentStatus.PLAYOFFS) {
            final Game nextPlayoffGameForUser = playoffService.getNextGameForUser(user);

            if (nextPlayoffGameForUser == null) {
                remainingOpponents = Collections.emptyList();
            } else {
                remainingOpponents = nextPlayoffGameForUser.getHomeUser().equals(user)
                        ? Collections.singletonList(nextPlayoffGameForUser.getAwayUser())
                        : Collections.singletonList(nextPlayoffGameForUser.getHomeUser());
            }
        } else {
            remainingOpponents = Collections.emptyList();
        }

        return remainingOpponents;
    }

    public User saveUser(final User user) {
        return userRepository.save(user);
    }

    public Page<User> findPaginated(int page, int size, Sort sort) {
        if (sort.equals(Sort.by(Sort.Direction.DESC, "userStats.trophyPoints"))
                || sort.equals(Sort.by(Sort.Direction.ASC, "userStats.trophyPoints"))) {
            sort = sort.and(Sort.by(Sort.Direction.DESC, "userStats.participations"));
        }
        sort = sort.and(Sort.by(Sort.Direction.ASC, "username"));
        return userRepository.findAll(PageRequest.of(page, size, sort));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public class UserExistsByEmailOrUsernameException extends RuntimeException {
    }

    public class InvalidUsernameException extends RuntimeException {
    }

    public class InvalidEmailException extends RuntimeException {
    }
}
