package com.cwtsite.cwt.domain.user.view.model;

import com.cwtsite.cwt.domain.user.repository.entity.User;

import java.util.List;

public class UserDetailDto {

    private Long id;
    private String username;
    private String country;
    private String about;
    private Boolean hasPic;
    private List<UserStatsDto> userStats;

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

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Boolean getHasPic() {
        return hasPic;
    }

    public void setHasPic(Boolean hasPic) {
        this.hasPic = hasPic;
    }

    public List<UserStatsDto> getUserStats() {
        return userStats;
    }

    public void setUserStats(List<UserStatsDto> userStats) {
        this.userStats = userStats;
    }

    public static UserDetailDto toDto(User user) {
        final UserDetailDto dto = new UserDetailDto();

        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setCountry(user.getUserProfile().getCountry());
//        dto.setHasPic(user.getUserProfile().getPicture()); // TODO
        dto.setHasPic(false);
        dto.setAbout(user.getUserProfile().getAbout());
        dto.setUserStats(UserStatsDto.toDtos(user.getUserStats()));

        return dto;
    }
}
