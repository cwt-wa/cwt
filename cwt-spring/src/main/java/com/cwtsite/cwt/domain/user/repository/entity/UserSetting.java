package com.cwtsite.cwt.domain.user.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "user_setting")
@SequenceGenerator(name = "user_setting_seq", sequenceName = "user_setting_seq", initialValue = 1332, allocationSize = 1)
public class UserSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_setting_seq")
    private Long id;

    @Column(name = "hide_profile")
    private Boolean hideProfile;

    @Column(name = "hide_email")
    private Boolean hideEmail;

    @Column(name = "modified")
    private Timestamp modified;

    @JsonIgnore
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

    public Timestamp getModified() {
        return modified;
    }

    public void setModified(Timestamp modified) {
        this.modified = modified;
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
