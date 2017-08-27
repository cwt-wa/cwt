package com.cwtsite.cwt.playoffs.service;

import com.cwtsite.cwt.game.entity.Game;
import com.cwtsite.cwt.game.service.GameRepository;
import com.cwtsite.cwt.tournament.entity.Tournament;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlayoffService {

    private final GameRepository gameRepository;

    @Autowired
    public PlayoffService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<Game> getGamesOfTournament(final Tournament tournament) {
        return gameRepository.findByTournamentAndPlayoffIsNotNull(tournament);
    }
}
