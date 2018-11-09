package com.cwtsite.cwt.domain.group.service;

import com.cwtsite.cwt.domain.group.entity.Group;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    List<Group> findByTournament(Tournament tournament);

    @Query("select g from Group g join g.standings s where s.user = :user and g.tournament = :tournament")
    Group findByTournamentAndUser(@Param("tournament") Tournament tournament, @Param("user") User user);

    int countByTournament(Tournament tournament);
}
