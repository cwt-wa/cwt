package com.cwtsite.cwt.game.service;

import com.cwtsite.cwt.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}
