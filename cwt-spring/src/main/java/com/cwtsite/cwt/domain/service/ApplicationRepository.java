package com.cwtsite.cwt.domain.service;

import com.cwtsite.cwt.entity.Application;
import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Application findByApplicantAndTournament(User applicant, Tournament tournament);

    List<Application> findByTournament(Tournament tournament);
}
