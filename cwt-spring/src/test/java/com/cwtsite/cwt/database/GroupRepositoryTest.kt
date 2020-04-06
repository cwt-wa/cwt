package com.cwtsite.cwt.database

import com.cwtsite.cwt.domain.group.entity.Group
import com.cwtsite.cwt.domain.group.service.GroupRepository
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.entity.GroupStanding
import com.cwtsite.cwt.integration.EmbeddedPostgres
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@DataJpaTest
@EmbeddedPostgres
class GroupRepositoryTest : AbstractDatabaseTest() {

    @Autowired
    private lateinit var groupRepository: GroupRepository

    @Test
    fun findUsers() {
        val tournament = em.persist(Tournament())

        val dummyUser1 = persistDummyUser()
        val dummyUser2 = persistDummyUser()
        val dummyUser3 = persistDummyUser()
        val dummyUser4 = persistDummyUser()

        em.persist(Group(
                tournament = tournament,
                standings = mutableListOf(
                        em.persist(GroupStanding(user = dummyUser1)),
                        em.persist(GroupStanding(user = dummyUser2)),
                        em.persist(GroupStanding(user = dummyUser3)),
                        em.persist(GroupStanding(user = dummyUser4)))))


        em.persist(Group(
                tournament = em.persist(Tournament()),
                standings = mutableListOf(
                        em.persist(GroupStanding(user = persistDummyUser())))))

        em.flush()

        Assertions
                .assertThat(groupRepository
                        .findAllGroupMembers(em.find(Tournament::class.java, tournament.id!!))
                        .map { it.id })
                .containsExactlyInAnyOrder(
                        dummyUser1.id!!, dummyUser2.id!!,
                        dummyUser3.id!!, dummyUser4.id!!)
    }

}
