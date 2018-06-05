package com.cwtsite.cwt.domain.user.view.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JwtUser<T extends GrantedAuthority> implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean activated;
    private final Date resetDate;
    private final List<String> roles;

    public JwtUser(Long id,
                   String username,
                   String email,
                   String password,
                   Collection<T> authorities,
                   boolean activated,
                   Date resetDate) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        // TODO Have a GrantedAuthority deserializer instead of mapping here.
        this.roles = authorities.stream()
                .map(a -> (T) a)
                .map(T::getAuthority)
                .collect(Collectors.toList());
        this.activated = activated;
        this.resetDate = resetDate;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public String getEmail() {
        return email;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    public List<String> getRoles() {
        return roles;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return activated;
    }

    @JsonIgnore
    public Date getResetDate() {
        return resetDate;
    }
}
