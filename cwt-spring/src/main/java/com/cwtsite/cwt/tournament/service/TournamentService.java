package com.cwtsite.cwt.tournament.service;

import com.cwtsite.cwt.application.service.ApplicationRepository;
import com.cwtsite.cwt.entity.Application;
import com.cwtsite.cwt.entity.Tournament;
import com.cwtsite.cwt.entity.enumeration.TournamentStatus;
import com.cwtsite.cwt.user.repository.UserRepository;
import com.cwtsite.cwt.user.repository.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                : new HashSet<>(userRepository.findAll(moderatorIds));

        Tournament tournament = new Tournament();

        tournament.getModerators().addAll(moderators);
        tournament.setHost(host);
        tournament.setStatus(TournamentStatus.OPEN);

        return tournamentRepository.save(tournament);
    }

    public Tournament getTournament(final long id) {
        return tournamentRepository.findOne(id);
    }

    public Tournament getCurrentTournament() {
        return tournamentRepository.findByStatusNot(TournamentStatus.ARCHIVED);
    }

    public List<Application> getApplicants(Tournament tournament) {
        return this.applicationRepository.findByTournament(tournament);
    }
}
