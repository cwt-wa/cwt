package com.cwtsite.cwt.domain.group.service

import com.cwtsite.cwt.entity.GroupStanding
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupStandingRepository : JpaRepository<GroupStanding, Long>
