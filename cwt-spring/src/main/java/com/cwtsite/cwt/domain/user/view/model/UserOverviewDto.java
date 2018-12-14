package com.cwtsite.cwt.domain.user.view.model;

import com.cwtsite.cwt.domain.user.repository.entity.User;

import java.util.List;

public class UserOverviewDto {

    private Long id;
    private String username;
    private String country;
    private Integer participations;
    private List<UserStatsDto> userStats;

    public static UserOverviewDto toDto(User user) {
        final UserOverviewDto dto = new UserOverviewDto();

        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setCountry(user.getCountry());
        dto.setParticipations(user.getUserStats().getParticipations());
        dto.setUserStats(UserStatsDto.toDtos(user.getUserStats()));

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getParticipations() {
        return participations;
    }

    public void setParticipations(Integer participations) {
        this.participations = participations;
    }

    public List<UserStatsDto> getUserStats() {
        return userStats;
    }

    public void setUserStats(List<UserStatsDto> userStats) {
        this.userStats = userStats;
    }
}
