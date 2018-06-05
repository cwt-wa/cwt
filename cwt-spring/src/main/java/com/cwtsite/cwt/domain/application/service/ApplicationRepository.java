package com.cwtsite.cwt.domain.application.service;

import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByTournament(Tournament tournament);
}
