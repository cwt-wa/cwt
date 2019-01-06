package com.cwtsite.cwt.domain.user.service

import com.btc.redg.generated.GUser
import com.btc.redg.generated.GUserStats
import com.btc.redg.generated.RedG
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.assertj.core.api.Assertions
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import test.AbstractDbTest

open class UserRepositoryTest : AbstractDbTest() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun findAll() {
        val redG = createRedG()

        createUserStats(redG, redG.addUser().id(1L).username("1L"), 1, 17)
        createUserStats(redG, redG.addUser().id(2L).username("2L"), 5, 2)
        redG.addUser().id(3L)

        insertRedGIntoDatabase(redG)

        Assertions
                .assertThat(findAllActual("userStats.participations").content.map { it.id })
                .containsExactly(2L, 1L, 3L)

        Assertions
                .assertThat(findAllActual("userStats.trophyPoints").content.map { it.id })
                .containsExactly(1L, 2L, 3L)
    }

    private fun findAllActual(sortColumn: String): Page<User> =
            userRepository.findAll(PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, sortColumn)))

    private fun createUserStats(redG: RedG, gUser: GUser, participations: Int, trophyPoints: Int): GUserStats =
            redG.addUserStats(gUser)
                    .participations(participations)
                    .trophyPoints(trophyPoints)
}
