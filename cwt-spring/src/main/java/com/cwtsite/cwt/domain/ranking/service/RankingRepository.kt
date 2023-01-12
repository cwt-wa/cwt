package com.cwtsite.cwt.domain.ranking.service

import com.cwtsite.cwt.domain.ranking.entity.Ranking
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RankingRepository : JpaRepository<Ranking, Long>
