package com.cwtsite.cwt.domain.schedule.entity

import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.hibernate.annotations.CreationTimestamp
import javax.persistence.*
import java.time.Instant

@Entity
@Table(name = "schedule")
data class Schedule(

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "schedule_id_seq")
        @SequenceGenerator(name = "schedule_id_seq", sequenceName = "schedule_id_seq", allocationSize = 3)
        val id: Long? = null,

        @ManyToOne(optional = false)
        @JoinColumn(name = "home_user_id", nullable = false)
        val homeUser: User,

        @ManyToOne(optional = false)
        @JoinColumn(name = "away_user_id", nullable = false)
        val awayUser: User,

        @Column(name = "appointment", nullable = false)
        val appointment: Instant,

        @ManyToOne(optional = false)
        @JoinColumn(name = "author_id", nullable = false)
        val author: User,

        @ManyToMany(cascade = [CascadeType.MERGE, CascadeType.PERSIST], fetch = FetchType.EAGER)
        @JoinTable(name = "schedule_channel", joinColumns = [JoinColumn(name = "schedule_id", referencedColumnName = "id")],
                inverseJoinColumns = [JoinColumn(name = "channel_id", referencedColumnName = "id")])
        val streams: MutableSet<Channel> = mutableSetOf(),

        @field:CreationTimestamp
        val created: Instant? = null
) {

    override fun toString() = "Schedule{id=$id, appointment=$appointment}"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Schedule

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
