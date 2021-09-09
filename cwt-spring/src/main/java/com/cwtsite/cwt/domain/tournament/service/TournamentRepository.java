package com.cwtsite.cwt.domain.tournament.service;

import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    long countByStatusNot(TournamentStatus status);

    Tournament findByStatusNot(TournamentStatus status);

    List<Tournament> findByStatus(TournamentStatus status);

    @Query("select t from Tournament t where year(t.created) = :year")
    Optional<Tournament> findByYear(@Param("year") Integer year);

    List<Tournament> findByCreatedBefore(Instant instant);
}
