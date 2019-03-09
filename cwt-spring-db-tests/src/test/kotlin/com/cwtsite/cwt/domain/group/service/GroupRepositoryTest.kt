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
        val gGroup = redG.addGroup()
                .tournamentIdTournament(gTournament)

        val dummyUser1 = redG.dummyUser()
        redG.addGroupStanding()
                .groupIdGroup(gGroup)
                .userIdUser(dummyUser1)
        val dummyUser2 = redG.dummyUser()
        redG.addGroupStanding()
                .groupIdGroup(gGroup)
                .userIdUser(dummyUser2)
        val dummyUser3 = redG.dummyUser()
        redG.addGroupStanding()
                .groupIdGroup(gGroup)
                .userIdUser(dummyUser3)
        val dummyUser4 = redG.dummyUser()
        redG.addGroupStanding()
                .groupIdGroup(gGroup)
                .userIdUser(dummyUser4)

        redG.addGroupStanding()
                .groupIdGroup(redG.dummyGroup().tournamentIdTournament(redG.dummyTournament()))
                .userIdUser(redG.dummyUser())

        insertRedGIntoDatabase(redG)

        Assertions
                .assertThat(groupRepository.findAllGroupMembers(em.find(Tournament::class.java, gTournament.id())).map { it.id })
                .containsExactlyInAnyOrder(dummyUser1.id(), dummyUser2.id(), dummyUser3.id(), dummyUser4.id())
    }

}
