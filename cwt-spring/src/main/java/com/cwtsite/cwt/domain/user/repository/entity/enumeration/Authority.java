package com.cwtsite.cwt.domain.user.repository.entity.enumeration;

import com.cwtsite.cwt.domain.user.repository.entity.AuthorityName;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "authority_id_seq", sequenceName = "authority_id_seq")
    private Long id;

    @Column(length = 50)
    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthorityName name;

    public Authority() {
    }

    public Authority(AuthorityName name) {
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
        return new Authority(authorityName);
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
