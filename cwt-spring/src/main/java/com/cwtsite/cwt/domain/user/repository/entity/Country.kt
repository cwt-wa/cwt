package com.cwtsite.cwt.domain.user.repository.entity

import org.hibernate.annotations.CreationTimestamp
import java.sql.Timestamp
import javax.persistence.*

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
        var created: Timestamp? = null
) {

    companion object {

        fun unknown() = Country(1, "Unknown", "unknown.png")
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
