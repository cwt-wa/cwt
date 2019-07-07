package com.cwtsite.cwt.domain.user.repository

import com.cwtsite.cwt.domain.user.repository.entity.Country
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CountryRepository : JpaRepository<Country, Long>
