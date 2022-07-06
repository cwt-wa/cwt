package com.cwtsite.cwt.domain.user.service;

import com.cwtsite.cwt.domain.user.repository.entity.User;
import com.cwtsite.cwt.domain.user.repository.entity.enumeration.Authority;
import com.cwtsite.cwt.domain.user.view.model.JwtUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Date;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

public class JwtUserFactory {

    private JwtUserFactory() {
    }

    public static JwtUser create(User user) {
        return new JwtUser<>(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                mapToGrantedAuthorities(user.getAuthorities()),
                user.getActivated(),
                Optional.ofNullable(user.getResetDate())
                        .map(d -> new Date(user.getResetDate().toEpochMilli()))
                        .orElse(null),
                user.getLocked()
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(List<Authority> authorities) {
        return authorities.stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName().name()))
                .collect(Collectors.toList());
    }
}
