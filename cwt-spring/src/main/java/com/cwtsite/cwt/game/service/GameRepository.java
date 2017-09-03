package com.cwtsite.cwt.game.service;

import com.cwtsite.cwt.game.entity.Game;
import com.cwtsite.cwt.tournament.entity.Tournament;
import com.cwtsite.cwt.user.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByTournamentAndPlayoffIsNotNull(Tournament tournament);

    @Query("select g from Game g where (g.homeUser = :user or g.awayUser = :user) and (g.homeUser is not null and g.awayUser is not null) and g.reporter is null and g.tournament = :tournament")
    Game findNextPlayoffGameForUser(@Param("tournament") Tournament tournament, @Param("user") User user);
}
