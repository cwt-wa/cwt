package com.cwtsite.cwt.domain.game.service;

import com.cwtsite.cwt.domain.game.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
}
