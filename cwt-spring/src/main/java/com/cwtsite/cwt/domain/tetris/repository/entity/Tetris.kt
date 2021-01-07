package com.cwtsite.cwt.domain.tetris.repository.entity

import com.cwtsite.cwt.domain.user.repository.entity.User
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "tetris")
data class Tetris(

        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tetris_id_seq")
        @SequenceGenerator(name = "tetris_id_seq", sequenceName = "tetris_id_seq", allocationSize = 15)
        val id: Long? = null,

        @ManyToOne(cascade = [CascadeType.MERGE, CascadeType.PERSIST])
        @JoinColumn(nullable = true)
        val user: User?,

        val guestname : String?,

        val highscore: Long,

        @field:CreationTimestamp
        val created: Instant? = null
) {

        override fun toString() = "Tetris{id=$id}"
}
