package com.cwtsite.cwt.domain.application.service;

import com.cwtsite.cwt.entity.Application;
import com.cwtsite.cwt.domain.tournament.service.TournamentService;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final TournamentService tournamentService;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository, TournamentService tournamentService) {
        this.applicationRepository = applicationRepository;
        this.tournamentService = tournamentService;
    }

    public Application apply(final User user) {
        return applicationRepository.save(new Application(tournamentService.getCurrentTournament(), user));
    }
}