package com.cwtsite.cwt.domain.playoffs.service;

import com.cwtsite.cwt.domain.game.entity.Game;
import com.cwtsite.cwt.domain.game.service.GameRepository;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus;
import com.cwtsite.cwt.domain.tournament.service.TournamentService;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PlayoffService {

    private final GameRepository gameRepository;
    private final TournamentService tournamentService;

    @Autowired
    public PlayoffService(GameRepository gameRepository, TournamentService tournamentService) {
        this.gameRepository = gameRepository;
        this.tournamentService = tournamentService;
    }

    public List<Game> getGamesOfTournament(final Tournament tournament) {
        return gameRepository.findByTournamentAndPlayoffIsNotNull(tournament);
    }

    public Game getNextGameForUser(final User user) {
        return gameRepository.findNextPlayoffGameForUser(tournamentService.getCurrentTournament(), user);
    }

    public boolean finalGamesAreNext() {
        final Tournament currentTournament = tournamentService.getCurrentTournament();

        if (currentTournament.getStatus() != TournamentStatus.PLAYOFFS) {
            return false;
        }

        final List<Game> playoffGames = gameRepository.findByTournamentAndPlayoffIsNotNull(currentTournament);

        final int numberOfGamesInFirstRound = playoffGames.stream()
                .filter(game -> game.getPlayoff().getRound() == 1)
                .collect(Collectors.toList())
                .size();

        final int numberOfRounds = (int) (Math.log(numberOfGamesInFirstRound) / Math.log(2)) + 1;

        return playoffGames.stream()
                .filter(g -> g.getPlayoff().getRound() == numberOfRounds || g.getPlayoff().getRound() == numberOfRounds + 1)
                .anyMatch(g -> g.getHomeUser() != null && g.getAwayUser() != null && g.getReporter() == null);
    }
}
