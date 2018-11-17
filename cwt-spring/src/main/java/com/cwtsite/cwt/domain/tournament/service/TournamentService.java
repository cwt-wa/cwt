package com.cwtsite.cwt.domain.tournament.service;

import com.cwtsite.cwt.domain.application.service.ApplicationRepository;
import com.cwtsite.cwt.domain.core.exception.NotFoundException;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus;
import com.cwtsite.cwt.domain.user.repository.UserRepository;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import com.cwtsite.cwt.entity.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TournamentService {

    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;
    private final ApplicationRepository applicationRepository;

    @Autowired
    public TournamentService(UserRepository userRepository, TournamentRepository tournamentRepository,
                             ApplicationRepository applicationRepository) {
        this.userRepository = userRepository;
        this.tournamentRepository = tournamentRepository;
        this.applicationRepository = applicationRepository;
    }

    public Tournament startNewTournament(final User host, final List<Long> moderatorIds) throws IllegalStateException {
        long numberOfUnarchivedTournaments = tournamentRepository.countByStatusNot(TournamentStatus.ARCHIVED);

        if (numberOfUnarchivedTournaments > 0) {
            throw new IllegalStateException("For a new tournament to start it is required that all tournaments are archived.");
        }

        final Set<User> moderators = moderatorIds == null || moderatorIds.isEmpty()
                ? Collections.emptySet()
                : new HashSet<>(userRepository.findAllById(moderatorIds));

        Tournament tournament = new Tournament();

        tournament.getModerators().addAll(moderators);
        tournament.setHost(host);
        tournament.setStatus(TournamentStatus.OPEN);

        return tournamentRepository.save(tournament);
    }

    public Optional<Tournament> getTournament(final long id) {
        return tournamentRepository.findById(id);
    }

    public Tournament getCurrentTournament() {
        final Tournament currentTournament = tournamentRepository.findByStatusNot(TournamentStatus.ARCHIVED);

        if (currentTournament == null) {
            throw new NotFoundException("There is currently no tournament");
        }

        return currentTournament;
    }

    public List<Application> getApplicants(Tournament tournament) {
        return this.applicationRepository.findByTournament(tournament);
    }

    public List<Tournament> getAll() {
        return tournamentRepository.findAll();
    }

    public Optional<Tournament> getTournamentByYear(long year) {
        return tournamentRepository.findByYear((int) year);
    }
}
