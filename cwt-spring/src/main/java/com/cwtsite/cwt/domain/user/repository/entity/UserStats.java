package com.cwtsite.cwt.domain.user.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class UserStats implements Serializable {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @JsonIgnore
    @MapsId
    @OneToOne(mappedBy = "userStats")
    @JoinColumn(name = "user_id")
    private User user;

    private String timeline;

    private Integer trophyPoints;

    private Integer participations;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public Integer getTrophyPoints() {
        return trophyPoints;
    }

    public void setTrophyPoints(Integer trophyPoints) {
        this.trophyPoints = trophyPoints;
    }

    public Integer getParticipations() {
        return participations;
    }

    public void setParticipations(Integer participations) {
        this.participations = participations;
    }
}
