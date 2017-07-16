package com.cwtsite.cwt.entity;

import com.cwtsite.cwt.user.repository.entity.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Rating.
 */
@Entity
@Table(name = "rating")
public class Rating implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "likes")
    private Boolean likes;

    @Column(name = "dislikes")
    private Boolean dislikes;

    @Column(name = "lightside")
    private Boolean lightside;

    @Column(name = "darkside")
    private Boolean darkside;

    @ManyToOne
    private User user;

    @ManyToOne
    private Game game;

    protected Rating() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean isLikes() {
        return likes;
    }

    public Rating likes(Boolean likes) {
        this.likes = likes;
        return this;
    }

    public void setLikes(Boolean likes) {
        this.likes = likes;
    }

    public Boolean isDislikes() {
        return dislikes;
    }

    public Rating dislikes(Boolean dislikes) {
        this.dislikes = dislikes;
        return this;
    }

    public void setDislikes(Boolean dislikes) {
        this.dislikes = dislikes;
    }

    public Boolean isLightside() {
        return lightside;
    }

    public Rating lightside(Boolean lightside) {
        this.lightside = lightside;
        return this;
    }

    public void setLightside(Boolean lightside) {
        this.lightside = lightside;
    }

    public Boolean isDarkside() {
        return darkside;
    }

    public Rating darkside(Boolean darkside) {
        this.darkside = darkside;
        return this;
    }

    public void setDarkside(Boolean darkside) {
        this.darkside = darkside;
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
                ", likes='" + likes + "'" +
                ", dislikes='" + dislikes + "'" +
                ", lightside='" + lightside + "'" +
                ", darkside='" + darkside + "'" +
                '}';
    }
}
