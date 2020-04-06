package com.cwtsite.cwt.domain.application.service;

import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import com.cwtsite.cwt.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByTournament(Tournament tournament);

    Optional<Application> findByApplicantAndTournament(User user, Tournament currentTournament);
}
