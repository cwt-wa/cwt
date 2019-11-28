package com.cwtsite.cwt.database

import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.assertj.core.api.Assertions
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import kotlin.test.Ignore

@Ignore
open class UserRepositoryTest : AbstractDatabaseTest() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun findAll() {
        val dummyUser1 = persistDummyUser()
        val dummyUser2 = persistDummyUser()

        // TODO Cannot insert into Materialize View
//        createUserStats(dummyUser1, 1, 17)
//        createUserStats(dummyUser2, 5, 2)

        persistDummyUser()

        em.flush()

        Assertions
                .assertThat(findAllActual("userStats.participations").content.map { it.id })
                .containsExactly(2L, 1L, 3L)

        Assertions
                .assertThat(findAllActual("userStats.trophyPoints").content.map { it.id })
                .containsExactly(1L, 2L, 3L)
    }

    private fun findAllActual(sortColumn: String): Page<User> =
            userRepository.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, sortColumn)))
}
