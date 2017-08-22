package com.cwtsite.cwt.group.service;

import com.cwtsite.cwt.entity.Tournament;
import com.cwtsite.cwt.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {

    List<Group> findByTournament(Tournament tournament);
}
