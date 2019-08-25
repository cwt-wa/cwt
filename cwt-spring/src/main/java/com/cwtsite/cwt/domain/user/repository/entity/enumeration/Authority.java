package com.cwtsite.cwt.domain.user.repository.entity.enumeration;

import com.cwtsite.cwt.domain.user.repository.entity.AuthorityName;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table
public class Authority {

    @Id
    private Long id;

    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthorityName name;

    public Authority() {
    }

    public Authority(Long id, @NotNull AuthorityName name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuthorityName getName() {
        return name;
    }

    public void setName(AuthorityName name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name.toString();
    }

    public static Authority fromName(AuthorityName authorityName) {
        return new Authority(authorityName.getId(), authorityName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Authority authority = (Authority) o;
        return id.equals(authority.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
