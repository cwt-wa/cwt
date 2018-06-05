package com.cwtsite.cwt.domain.user.repository;

import com.cwtsite.cwt.domain.user.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
