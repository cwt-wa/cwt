package com.cwtsite.cwt.entity;

import com.cwtsite.cwt.entity.enumeration.TournamentStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A Tournament.
 */
@Entity
@Table(name = "tournament")
public class Tournament implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TournamentStatus status;

    @Lob
    @Column(name = "review")
    private String review;

    @Column(name = "open")
    private LocalDate open;

    @Column(name = "created")
    private LocalDate created;

    @ManyToOne
    private User host;

    @ManyToOne
    private User bronzeWinner;

    @ManyToOne
    private User silverWinner;

    @ManyToOne
    private User goldWinner;

    @ManyToMany
    @JoinTable(name = "tournament_moderator",
            joinColumns = @JoinColumn(name = "tournaments_id", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "moderators_id", referencedColumnName = "ID"))
    private Set<User> moderators = new HashSet<>();

    protected Tournament() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    public Tournament status(TournamentStatus status) {
        this.status = status;
        return this;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Tournament review(String review) {
        this.review = review;
        return this;
    }

    public LocalDate getOpen() {
        return open;
    }

    public void setOpen(LocalDate open) {
        this.open = open;
    }

    public Tournament open(LocalDate open) {
        this.open = open;
        return this;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public Tournament created(LocalDate created) {
        this.created = created;
        return this;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User user) {
        this.host = user;
    }

    public Tournament host(User user) {
        this.host = user;
        return this;
    }

    public User getBronzeWinner() {
        return bronzeWinner;
    }

    public void setBronzeWinner(User user) {
        this.bronzeWinner = user;
    }

    public Tournament bronzeWinner(User user) {
        this.bronzeWinner = user;
        return this;
    }

    public User getSilverWinner() {
        return silverWinner;
    }

    public void setSilverWinner(User user) {
        this.silverWinner = user;
    }

    public Tournament silverWinner(User user) {
        this.silverWinner = user;
        return this;
    }

    public User getGoldWinner() {
        return goldWinner;
    }

    public void setGoldWinner(User user) {
        this.goldWinner = user;
    }

    public Tournament goldWinner(User user) {
        this.goldWinner = user;
        return this;
    }

    public Set<User> getModerators() {
        return moderators;
    }

    public void setModerators(Set<User> users) {
        this.moderators = users;
    }

//    TODO Fucked up ManyToMany?
//    public Tournament addModerator(User user) {
//        moderators.add(user);
//        user.getTournaments().add(this);
//        return this;
//    }

//    TODO Fucked up ManyToMany?
//    public Tournament removeModerator(User user) {
//        moderators.remove(user);
//        user.getTournaments().remove(this);
//        return this;
//    }

    public Tournament moderators(Set<User> users) {
        this.moderators = users;
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
        Tournament tournament = (Tournament) o;
        if (tournament.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, tournament.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "id=" + id +
                ", status='" + status + "'" +
                ", review='" + review + "'" +
                ", open='" + open + "'" +
                ", created='" + created + "'" +
                '}';
    }
}