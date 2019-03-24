package com.cwtsite.cwt.domain.group.service

import com.cwtsite.cwt.domain.tournament.entity.Tournament
import org.assertj.core.api.Assertions
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import test.AbstractDbTest


open class GroupRepositoryTest : AbstractDbTest() {

    @Autowired
    private lateinit var groupRepository: GroupRepository

    @Test
    fun findUsers() {
        val redG = createRedG()

        val gTournament = redG.addTournament()
        val gGroup = redG.addGroup(gTournament)

        val dummyUser1 = redG.dummyUser()
        redG.addGroupStanding(dummyUser1).groupIdGroup(gGroup)

        val dummyUser2 = redG.dummyUser()
        redG.addGroupStanding(dummyUser2).groupIdGroup(gGroup)

        val dummyUser3 = redG.dummyUser()
        redG.addGroupStanding(dummyUser3).groupIdGroup(gGroup)

        val dummyUser4 = redG.dummyUser()
        redG.addGroupStanding(dummyUser4).groupIdGroup(gGroup)


        redG.addGroupStanding(redG.dummyUser())
                .groupIdGroup(redG.dummyGroup().tournamentIdTournament(redG.dummyTournament()))

        insertRedGIntoDatabase(redG)

        Assertions
                .assertThat(groupRepository.findAllGroupMembers(em.find(Tournament::class.java, gTournament.id())).map { it.id })
                .containsExactlyInAnyOrder(dummyUser1.id(), dummyUser2.id(), dummyUser3.id(), dummyUser4.id())
    }

}
