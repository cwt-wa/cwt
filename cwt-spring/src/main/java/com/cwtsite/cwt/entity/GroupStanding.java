package com.cwtsite.cwt.entity;

import com.cwtsite.cwt.user.repository.entity.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Materialized View?
 */
@Entity
@Table(name = "group_standing")
public class GroupStanding implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "points")
    private Integer points;

    @Column(name = "games")
    private Integer games;

    @Column(name = "game_ratio")
    private Integer gameRatio;

    @Column(name = "round_ratio")
    private Integer roundRatio;

    @ManyToOne
    private Group group;

    @ManyToOne
    private User user;

    protected GroupStanding() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public GroupStanding points(Integer points) {
        this.points = points;
        return this;
    }

    public Integer getGames() {
        return games;
    }

    public void setGames(Integer games) {
        this.games = games;
    }

    public GroupStanding games(Integer games) {
        this.games = games;
        return this;
    }

    public Integer getGameRatio() {
        return gameRatio;
    }

    public void setGameRatio(Integer gameRatio) {
        this.gameRatio = gameRatio;
    }

    public GroupStanding gameRatio(Integer gameRatio) {
        this.gameRatio = gameRatio;
        return this;
    }

    public Integer getRoundRatio() {
        return roundRatio;
    }

    public void setRoundRatio(Integer roundRatio) {
        this.roundRatio = roundRatio;
    }

    public GroupStanding roundRatio(Integer roundRatio) {
        this.roundRatio = roundRatio;
        return this;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public GroupStanding group(Group group) {
        this.group = group;
        return this;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GroupStanding user(User user) {
        this.user = user;
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
        GroupStanding groupStanding = (GroupStanding) o;
        if (groupStanding.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, groupStanding.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "GroupStanding{" +
                "id=" + id +
                ", points='" + points + "'" +
                ", games='" + games + "'" +
                ", gameRatio='" + gameRatio + "'" +
                ", roundRatio='" + roundRatio + "'" +
                '}';
    }
}
