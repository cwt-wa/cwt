package com.cwtsite.cwt.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Materialized View?
 */
@Entity
@Table(name = "rating_result")
public class RatingResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "likes")
    private Integer likes;

    @Column(name = "dislikes")
    private Integer dislikes;

    @Column(name = "lightside")
    private Integer lightside;

    @Column(name = "darkside")
    private Integer darkside;

    @OneToOne
    @JoinColumn(unique = true)
    private Game game;

    protected RatingResult() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public RatingResult likes(Integer likes) {
        this.likes = likes;
        return this;
    }

    public Integer getDislikes() {
        return dislikes;
    }

    public void setDislikes(Integer dislikes) {
        this.dislikes = dislikes;
    }

    public RatingResult dislikes(Integer dislikes) {
        this.dislikes = dislikes;
        return this;
    }

    public Integer getLightside() {
        return lightside;
    }

    public void setLightside(Integer lightside) {
        this.lightside = lightside;
    }

    public RatingResult lightside(Integer lightside) {
        this.lightside = lightside;
        return this;
    }

    public Integer getDarkside() {
        return darkside;
    }

    public void setDarkside(Integer darkside) {
        this.darkside = darkside;
    }

    public RatingResult darkside(Integer darkside) {
        this.darkside = darkside;
        return this;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public RatingResult game(Game game) {
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
        RatingResult ratingResult = (RatingResult) o;
        if (ratingResult.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, ratingResult.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "RatingResult{" +
                "id=" + id +
                ", likes='" + likes + "'" +
                ", dislikes='" + dislikes + "'" +
                ", lightside='" + lightside + "'" +
                ", darkside='" + darkside + "'" +
                '}';
    }
}
