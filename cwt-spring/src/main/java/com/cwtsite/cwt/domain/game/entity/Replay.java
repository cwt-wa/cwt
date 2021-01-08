package com.cwtsite.cwt.domain.game.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.util.Objects;


@Entity
public class Replay {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "replay_id_seq")
    @SequenceGenerator(name = "replay_id_seq", sequenceName = "replay_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private byte[] file;

    @Column(nullable = false)
    private String mediaType;

    @Column(nullable = false)
    private String extension;

    public Replay() {
    }

    public Replay(byte[] file, String mediaType, String extension) {
        this.file = file;
        this.mediaType = mediaType;
        this.extension = extension;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        return "Replay{id=" + id + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Replay replay = (Replay) o;
        return id.equals(replay.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
