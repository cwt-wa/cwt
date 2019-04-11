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
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByTournamentAndPlayoffIsNotNull(Tournament tournament);

    @Query("select g from Game g where (g.homeUser = :user or g.awayUser = :user) and (g.homeUser is not null " +
            "and g.awayUser is not null) and g.reporter is null and g.tournament = :tournament and g.group is null")
    Game findNextPlayoffGameForUser(
            @Param("tournament") Tournament tournament,
            @Param("user") User user);

    @Query("select g from Game g where (g.homeUser = :user or g.awayUser = :user) and (g.homeUser is not null " +
            "and g.awayUser is not null) and g.reporter is not null and g.group = :group")
    List<Game> findPlayedByUserInGroup(
            @Param("user") User user,
            @Param("group") Group group);

    @Query("select g from Game g where tournament = :tournament and g.playoff.round = :round and g.playoff.spot = :spot")
    Optional<Game> findGameInPlayoffTree(
            @Param("tournament") Tournament tournament,
            @Param("round") int round,
            @Param("spot") int spot);

    @Query("select g from Game g where g.playoff.round >= :finalRound and g.homeUser is not null and g.awayUser is not null")
    List<Game> findReadyGamesInRoundEqualOrGreaterThan(@Param("finalRound") int finalRound);

    @Query("select g from Game g where g.playoff.round >= :round and g.tournament = :tournament")
    List<Game> findByTournamentAndRound(@Param("tournament") Tournament tournament,
                                        @Param("round") Integer round);

    List<Game> findByGroup(Group group);
}
