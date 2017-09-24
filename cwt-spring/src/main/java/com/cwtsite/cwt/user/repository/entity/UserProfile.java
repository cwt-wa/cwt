package com.cwtsite.cwt.user.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * A UserProfile.
 */
@Entity
@Table(name = "user_profile")
public class UserProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "modified")
    private Timestamp modified;

    @Column(name = "country")
    private String country;

    @Column(name = "clan")
    private String clan;

    @Column(name = "email")
    private String email;

    @Column(name = "skype")
    private String skype;

    @Column(name = "facebook")
    private String facebook;

    @Column(name = "twitter")
    private String twitter;

    @Column(name = "about", columnDefinition = "text")
    private String about;

    @JsonIgnore
    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    public UserProfile() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getModified() {
        return modified;
    }

    public void setModified(Timestamp modified) {
        this.modified = modified;
    }

    public UserProfile modified(Timestamp modified) {
        this.modified = modified;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public UserProfile country(String country) {
        this.country = country;
        return this;
    }

    public String getClan() {
        return clan;
    }

    public void setClan(String clan) {
        this.clan = clan;
    }

    public UserProfile clan(String clan) {
        this.clan = clan;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserProfile email(String email) {
        this.email = email;
        return this;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public UserProfile skype(String skype) {
        this.skype = skype;
        return this;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public UserProfile facebook(String facebook) {
        this.facebook = facebook;
        return this;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public UserProfile twitter(String twitter) {
        this.twitter = twitter;
        return this;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public UserProfile about(String about) {
        this.about = about;
        return this;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserProfile user(User user) {
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
        UserProfile userProfile = (UserProfile) o;
        if (userProfile.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, userProfile.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", modified='" + modified + "'" +
                ", country='" + country + "'" +
                ", clan='" + clan + "'" +
                ", email='" + email + "'" +
                ", skype='" + skype + "'" +
                ", facebook='" + facebook + "'" +
                ", twitter='" + twitter + "'" +
                ", about='" + about + "'" +
                '}';
    }
}
