package com.cwtsite.cwt.domain.configuration.entity


import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.hibernate.annotations.UpdateTimestamp
import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "configuration")
data class Configuration(

        @Id
        @Column(name = "key")
        @Enumerated(EnumType.STRING)
        var key: ConfigurationKey,

        @Column(name = "value", columnDefinition = "text")
        var value: String,

        @Column(name = "description", columnDefinition = "text")
        var description: String? = null,

        @Column(name = "modified")
        @field:UpdateTimestamp
        var modified: Timestamp? = null,

        @ManyToOne
        var author: User
)
