package com.cwtsite.cwt.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A PlayoffGame.
 */
@Entity
@Table(name = "playoff_game")
public class PlayoffGame implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "round")
    private Integer round;

    @Column(name = "spot")
    private Integer spot;

    protected PlayoffGame() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public PlayoffGame round(Integer round) {
        this.round = round;
        return this;
    }

    public Integer getSpot() {
        return spot;
    }

    public void setSpot(Integer spot) {
        this.spot = spot;
    }

    public PlayoffGame spot(Integer spot) {
        this.spot = spot;
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
        PlayoffGame playoffGame = (PlayoffGame) o;
        if (playoffGame.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, playoffGame.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PlayoffGame{" +
                "id=" + id +
                ", round='" + round + "'" +
                ", spot='" + spot + "'" +
                '}';
    }
}
