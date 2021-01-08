package com.cwtsite.cwt.domain.user.repository.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GenerationType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.SequenceGenerator

@Entity
data class Photo(

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "photo_id_seq")
        @SequenceGenerator(name = "photo_id_seq", sequenceName = "photo_id_seq", allocationSize = 1)
        var id: Long? = null,

        @Column(nullable = false)
        var file: ByteArray,

        @Column(nullable = false)
        var mediaType: String,

        @Column(nullable = false)
        var extension: String
) {

    override fun toString() = "Photo{id=$id}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Photo

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
