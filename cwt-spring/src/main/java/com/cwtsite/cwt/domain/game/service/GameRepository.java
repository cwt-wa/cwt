package com.cwtsite.cwt.domain.game.service;

import com.cwtsite.cwt.domain.game.entity.Game;
import com.cwtsite.cwt.domain.group.entity.Group;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByTournamentAndPlayoffIsNotNull(Tournament tournament);

    @Query("select g from Game g where (g.homeUser = :user or g.awayUser = :user) and (g.homeUser is not null " +
            "and g.awayUser is not null) and g.reporter is null and g.tournament = :tournament and g.group is null")
    Game findNextPlayoffGameForUser(
            @Param("tournament") Tournament tournament,
            @Param("user") User user);

    @Query("select g from Game g where (g.homeUser = :user or g.awayUser = :user) and (g.homeUser is not null " +
            "and g.awayUser is not null) and g.reporter is not null and g.tournament = :tournament and g.group = :group")
    List<Game> findReportedGames(
            @Param("tournament") Tournament tournament,
            @Param("user") User user,
            @Param("group") Group group);
}
