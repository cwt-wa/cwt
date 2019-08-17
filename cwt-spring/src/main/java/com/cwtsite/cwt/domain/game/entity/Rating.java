package com.cwtsite.cwt.domain.game.entity;

import com.cwtsite.cwt.domain.game.entity.enumeration.RatingType;
import com.cwtsite.cwt.domain.user.repository.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "rating")
@SequenceGenerator(name = "rating_seq", sequenceName = "rating_seq", allocationSize = 1)
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rating_seq")
    private Long id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private RatingType type;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private User user;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Game game;

    @Column(name = "modified", nullable = false)
    @UpdateTimestamp
    private Timestamp modified;

    public Rating(RatingType type, User user, Game game) {
        this.type = type;
        this.user = user;
        this.game = game;
    }

    protected Rating() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RatingType getType() {
        return type;
    }

    public void setType(RatingType type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Rating user(User user) {
        this.user = user;
        return this;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Rating game(Game game) {
        this.game = game;
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
        Rating rating = (Rating) o;
        if (rating.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, rating.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Rating{" +
                "id=" + id +
                ", type='" + type.name() + "'}";
    }
}
