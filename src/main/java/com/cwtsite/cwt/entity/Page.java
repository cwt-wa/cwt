package com.cwtsite.cwt.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Page.
 */
@Entity
@Table(name = "page")
public class Page implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "text")
    private String text;

    @Column(name = "created")
    private LocalDate created;

    @Column(name = "modified")
    private LocalDate modified;

    @ManyToOne
    private User author;

    protected Page() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Page title(String title) {
        this.title = title;
        return this;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Page text(String text) {
        this.text = text;
        return this;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public Page created(LocalDate created) {
        this.created = created;
        return this;
    }

    public LocalDate getModified() {
        return modified;
    }

    public void setModified(LocalDate modified) {
        this.modified = modified;
    }

    public Page modified(LocalDate modified) {
        this.modified = modified;
        return this;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User user) {
        this.author = user;
    }

    public Page author(User user) {
        this.author = user;
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
        Page page = (Page) o;
        if (page.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, page.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Page{" +
                "id=" + id +
                ", title='" + title + "'" +
                ", text='" + text + "'" +
                ", created='" + created + "'" +
                ", modified='" + modified + "'" +
                '}';
    }
}
