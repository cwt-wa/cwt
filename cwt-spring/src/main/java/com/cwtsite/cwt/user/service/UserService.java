package com.cwtsite.cwt.user.service;

import com.cwtsite.cwt.application.service.ApplicationRepository;
import com.cwtsite.cwt.entity.GroupStanding;
import com.cwtsite.cwt.game.entity.Game;
import com.cwtsite.cwt.group.entity.Group;
import com.cwtsite.cwt.group.service.GroupRepository;
import com.cwtsite.cwt.playoffs.service.PlayoffService;
import com.cwtsite.cwt.tournament.entity.Tournament;
import com.cwtsite.cwt.tournament.entity.enumeration.TournamentStatus;
import com.cwtsite.cwt.tournament.service.TournamentService;
import com.cwtsite.cwt.user.repository.UserRepository;
import com.cwtsite.cwt.user.repository.entity.AuthorityName;
import com.cwtsite.cwt.user.repository.entity.User;
import com.cwtsite.cwt.user.repository.entity.UserProfile;
import com.cwtsite.cwt.user.repository.entity.UserSetting;
import com.cwtsite.cwt.user.view.model.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserService {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final TournamentService tournamentService;
    private final ApplicationRepository applicationRepository;
    private final GroupRepository groupRepository;
    private final PlayoffService playoffService;

    @Autowired
    public UserService(UserRepository userRepository, AuthService authService, TournamentService tournamentService,
                       ApplicationRepository applicationRepository, GroupRepository groupRepository,
                       PlayoffService playoffService) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.tournamentService = tournamentService;
        this.applicationRepository = applicationRepository;
        this.groupRepository = groupRepository;
        this.playoffService = playoffService;
    }

    @Transactional
    public User registerUser(UserRegistrationDto userRegistrationDto) {
        return userRepository.save(map(userRegistrationDto));
    }

    private User map(UserRegistrationDto dto) {
        User user = new User(new UserProfile(), new UserSetting(), AuthorityName.ROLE_USER);

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(authService.createHash(dto.getPassword()));
        user.setActivated(true);

        return user;
    }

    public Boolean userCanApplyForCurrentTournament(User user) {
        Tournament currentTournament = tournamentService.getCurrentTournament();

        return currentTournament != null && TournamentStatus.OPEN == currentTournament.getStatus()
                && applicationRepository.findByApplicantAndTournament(user, currentTournament) == null;
    }

    public List<User> getAllOrderedByUsername() {
        return userRepository.findAll(new Sort("username"));
    }

    public User getById(long id) {
        return userRepository.findOne(id);
    }

    public List<User> getByIds(List<Long> ids) {
        return userRepository.findAll(ids);
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
        List<User> remainingOpponents;

        if (currentTournament.getStatus() == TournamentStatus.GROUP) {
            // TODO Reduce/filter group members by those already played against.

            remainingOpponents = group.getStandings().stream()
                    .filter(groupStanding -> !groupStanding.getUser().equals(user))
                    .map(GroupStanding::getUser)
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
}
