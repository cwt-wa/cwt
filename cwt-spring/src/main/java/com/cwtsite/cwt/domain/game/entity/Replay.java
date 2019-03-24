package com.cwtsite.cwt.domain.game.entity;

import javax.persistence.*;


@Entity
public class Replay {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
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
}
