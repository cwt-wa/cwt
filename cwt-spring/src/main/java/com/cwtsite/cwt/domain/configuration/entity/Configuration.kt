package com.cwtsite.cwt.domain.configuration.entity


import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "configuration")
data class Configuration(

        @Id
        @Column(name = "key")
        @Enumerated(EnumType.STRING)
        var key: ConfigurationKey,

        @Column(name = "value", columnDefinition = "text")
        var value: String?,

        @Column(name = "description", columnDefinition = "text")
        var description: String? = null,

        @Column(name = "modified")
        @field:UpdateTimestamp
        var modified: Instant? = null,

        @ManyToOne(optional = false, fetch = FetchType.LAZY)
        @JoinColumn(nullable = false)
        var author: User? = null
) {
    override fun toString(): String {
        return "Configuration{id=$key, value=$value}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Configuration

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }
}
