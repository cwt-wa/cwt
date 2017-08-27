package com.cwtsite.cwt.game.service;

import com.cwtsite.cwt.game.entity.Game;
import com.cwtsite.cwt.tournament.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByTournamentAndPlayoffIsNotNull(Tournament tournament);
}
