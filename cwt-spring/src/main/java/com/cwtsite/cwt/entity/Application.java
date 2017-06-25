package com.cwtsite.cwt.entity;

import com.cwtsite.cwt.user.repository.entity.User;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Application.
 */
@Entity
@Table(name = "application")
public class Application implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "created")
    private LocalDate created;

    @Column(name = "revoked")
    private Boolean revoked;

    @ManyToOne
    private Tournament tournament;

    @ManyToOne
    private User applicant;

    protected Application() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public Application created(LocalDate created) {
        this.created = created;
        return this;
    }

    public Boolean isRevoked() {
        return revoked;
    }

    public Application revoked(Boolean revoked) {
        this.revoked = revoked;
        return this;
    }

    public void setRevoked(Boolean revoked) {
        this.revoked = revoked;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Application tournament(Tournament tournament) {
        this.tournament = tournament;
        return this;
    }

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User user) {
        this.applicant = user;
    }

    public Application applicant(User user) {
        this.applicant = user;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Application application = (Application) o;
        if (application.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, application.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Application{" +
                "id=" + id +
                ", created='" + created + "'" +
                ", revoked='" + revoked + "'" +
                '}';
    }
}
