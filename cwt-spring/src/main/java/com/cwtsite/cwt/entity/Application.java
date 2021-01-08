package com.cwtsite.cwt.entity;

import com.cwtsite.cwt.domain.tournament.entity.Tournament;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "application")
@SequenceGenerator(name = "application_seq", sequenceName = "application_id_seq", allocationSize = 1)
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "application_seq")
    private Long id;

    @Column(name = "created", nullable = false)
    @CreationTimestamp
    private Instant created;

    @Column(name = "revoked")
    private Boolean revoked;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Tournament tournament;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private User applicant;

    protected Application() {
    }

    public Application(Tournament tournament, User applicant) {
        this.tournament = tournament;
        this.applicant = applicant;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Application created(Instant created) {
        this.created = created;
        return this;
    }

    public Boolean isRevoked() {
        return revoked;
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

    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User user) {
        this.applicant = user;
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
