package com.cwtsite.cwt.group.service;

import com.cwtsite.cwt.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
