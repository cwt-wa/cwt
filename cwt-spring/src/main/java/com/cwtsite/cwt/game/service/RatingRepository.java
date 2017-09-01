package com.cwtsite.cwt.game.service;

import com.cwtsite.cwt.game.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
