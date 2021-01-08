package com.cwtsite.cwt.domain.user.repository.entity

import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GenerationType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table(name = "country")
data class Country(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "country_id_seq")
        @SequenceGenerator(name = "country_id_seq", sequenceName = "country_id_seq", allocationSize = 1)
        val id: Long? = null,

        @Column(nullable = false)
        val name: String,

        @Column(nullable = false)
        val flag: String,

        @field:CreationTimestamp
        var created: Instant? = null
) {

    companion object {

        fun unknown() = Country(1, "Unknown", "unknown.png")
    }

    override fun toString(): String {
        return "Country{id=$id}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Country

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
