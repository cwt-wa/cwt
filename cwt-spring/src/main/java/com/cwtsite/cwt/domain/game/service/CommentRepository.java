package com.cwtsite.cwt.domain.game.service;

import com.cwtsite.cwt.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
