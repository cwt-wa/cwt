package com.cwtsite.cwt.domain.game.service;

import com.cwtsite.cwt.domain.game.entity.GameStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameStatsRepository extends JpaRepository<GameStats, Long> {
}
