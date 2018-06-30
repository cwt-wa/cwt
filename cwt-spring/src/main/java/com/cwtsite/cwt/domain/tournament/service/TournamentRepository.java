package com.cwtsite.cwt.domain.tournament.service;

import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    long countByStatusNot(TournamentStatus status);

    Tournament findByStatusNot(TournamentStatus status);

    @Query("select t from Tournament t where year(t.created) = :year")
    Optional<Tournament> findByYear(@Param("year") Integer year);
}
