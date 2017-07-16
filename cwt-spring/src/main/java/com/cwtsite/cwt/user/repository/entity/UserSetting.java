package com.cwtsite.cwt.user.repository.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A UserSetting.
 */
@Entity
@Table(name = "user_setting")
public class UserSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "hide_profile")
    private Boolean hideProfile;

    @Column(name = "hide_email")
    private Boolean hideEmail;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    public UserSetting() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean isHideProfile() {
        return hideProfile;
    }

    public UserSetting hideProfile(Boolean hideProfile) {
        this.hideProfile = hideProfile;
        return this;
    }

    public void setHideProfile(Boolean hideProfile) {
        this.hideProfile = hideProfile;
    }

    public Boolean isHideEmail() {
        return hideEmail;
    }

    public UserSetting hideEmail(Boolean hideEmail) {
        this.hideEmail = hideEmail;
        return this;
    }

    public void setHideEmail(Boolean hideEmail) {
        this.hideEmail = hideEmail;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserSetting user(User user) {
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
        UserSetting userSetting = (UserSetting) o;
        if (userSetting.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, userSetting.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "UserSetting{" +
                "id=" + id +
                ", hideProfile='" + hideProfile + "'" +
                ", hideEmail='" + hideEmail + "'" +
                '}';
    }
}
