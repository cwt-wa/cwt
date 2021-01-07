package com.cwtsite.cwt.domain.user.repository.entity

import com.cwtsite.cwt.domain.user.repository.entity.enumeration.Authority
import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import java.time.Instant

@Entity
@Table(name = "\"user\"")
@SequenceGenerator(name = "user_seq", sequenceName = "user_id_seq", allocationSize = 1)
data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
        val id: Long? = null,

        @JsonIgnore
        @Size(min = 60, max = 60)
        @Column(name = "password_hash", length = 60)
        var password: String? = null,

        @JsonIgnore
        @Size(min = 40, max = 40)
        @Column(name = "password_legacy_hash", length = 40)
        var password_legacy: String? = null,

        @Size(max = 16, min = 3)
        @Column(length = 16, unique = true, nullable = false)
        var username: String,

        @Size(max = 100)
        @Column(length = 100, unique = true)
        var email: String,

        @NotNull
        @Column(nullable = false)
        var activated: Boolean = true,

        @Size(max = 20)
        @Column(name = "activation_key", length = 20, nullable = true)
        @JsonIgnore
        var activationKey: String? = null,

        @Size(max = 20)
        @Column(name = "reset_key", length = 20)
        var resetKey: String? = null,

        @Column(name = "reset_date", length = 20)
        var resetDate: Instant? = null,

        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(name = "USER_AUTHORITY", joinColumns = [JoinColumn(name = "USER_ID", referencedColumnName = "ID")],
                inverseJoinColumns = [JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID")])
        var authorities: MutableList<Authority> = mutableListOf(Authority.fromName(AuthorityName.ROLE_USER)),

        @ManyToOne(optional = false)
        var country: Country = Country.unknown(),

        @Column(columnDefinition = "text")
        var about: String? = null,

        @OneToOne(cascade = [CascadeType.ALL])
        @PrimaryKeyJoinColumn
        var userStats: UserStats? = null,

        @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
        var photo: Photo? = null,

        @field:CreationTimestamp
        var created: Instant? = null,

        @field:UpdateTimestamp
        var modified: Instant? = null
) {
    fun isAdmin() = this.authorities.map { it.name }.contains(AuthorityName.ROLE_ADMIN)

    override fun toString() = "User{id=$id, username=$username}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
