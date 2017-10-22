package com.cwtsite.cwt.configuration.entity;


import com.cwtsite.cwt.configuration.entity.enumeratuion.ConfigurationKey;
import com.cwtsite.cwt.user.repository.entity.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "configuration")
public class Configuration implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "key")
    @Enumerated(EnumType.STRING)
    private ConfigurationKey key;

    @Column(name = "value", columnDefinition = "text")
    private String value;

    @Column(name = "modified")
    @UpdateTimestamp
    private Timestamp modified;

    @ManyToOne(optional = false)
    private User author;

    public ConfigurationKey getKey() {
        return key;
    }

    public void setKey(ConfigurationKey key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Timestamp getModified() {
        return modified;
    }

    public void setModified(Timestamp modified) {
        this.modified = modified;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
