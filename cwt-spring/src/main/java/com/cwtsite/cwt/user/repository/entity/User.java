package com.cwtsite.cwt.user.repository.entity;

import com.cwtsite.cwt.user.repository.entity.enumeration.Authority;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user_")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonIgnore
    @NotNull
    @Size(min = 60, max = 60)
    @Column(name = "password_hash", length = 60)
    private String password;

    @JsonIgnore
    @Size(min = 40, max = 40)
    @Column(name = "password_legacy_hash", length = 40)
    private String password_legacy;

    @Size(max = 16, min = 3)
    @Column(length = 16, unique = true, nullable = false)
    private String username;

    @Email
    @Size(max = 100)
    @Column(length = 100, unique = true, nullable = false)
    private String email;

    @NotNull
    @Column(nullable = false)
    private boolean activated = false;

    @Size(max = 20)
    @Column(name = "activation_key", length = 20)
    @JsonIgnore
    private String activationKey;

    @Size(max = 20)
    @Column(name = "reset_key", length = 20)
    private String resetKey;

    @Column(name = "reset_date", nullable = true)
    private Date resetDate;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "USER_AUTHORITY",
            joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
            inverseJoinColumns = {@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")})
    private List<Authority> authorities;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = false, fetch = FetchType.EAGER, mappedBy = "user")
    @JoinColumn(unique = true)
    private UserProfile userProfile;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = false, fetch = FetchType.EAGER, mappedBy = "user")
    @JoinColumn(unique = true)
    private UserSetting userSetting;

    protected User() {
    }

    public User(UserProfile userProfile, UserSetting userSetting) {
        this.userProfile = userProfile;
        this.userSetting = userSetting;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword_legacy() {
        return password_legacy;
    }

    public void setPassword_legacy(String password_legacy) {
        this.password_legacy = password_legacy;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getActivationKey() {
        return activationKey;
    }

    public void setActivationKey(String activationKey) {
        this.activationKey = activationKey;
    }

    public String getResetKey() {
        return resetKey;
    }

    public void setResetKey(String resetKey) {
        this.resetKey = resetKey;
    }

    public Date getResetDate() {
        return resetDate;
    }

    public void setResetDate(Date resetDate) {
        this.resetDate = resetDate;
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public UserSetting getUserSetting() {
        return userSetting;
    }

    public void setUserSetting(UserSetting userSetting) {
        this.userSetting = userSetting;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", authorities='" + authorities.toString() + '\'' +
                ", resetDate='" + resetDate + '\'' +
                ", resetKey='" + resetKey + '\'' +
                ", activated='" + activated + '\'' +
                ", activationKey='" + activationKey + '\'' +
                "}";
    }
}
