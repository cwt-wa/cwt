package com.cwtsite.cwt.domain.group.service

import com.cwtsite.cwt.domain.configuration.service.ConfigurationService
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.group.entity.Group
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.entity.GroupStanding
import com.cwtsite.cwt.test.EntityDefaults
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GroupServiceTest {

    @InjectMocks private lateinit var groupService: GroupService
    @Mock private lateinit var groupRepository: GroupRepository
    @Mock private lateinit var groupStandingRepository: GroupStandingRepository
    @Mock private lateinit var tournamentRepository: TournamentRepository
    @Mock private lateinit var configurationService: ConfigurationService

    @Test
    fun calcTableByGame() {
        val game = Game()

        val user1 = EntityDefaults.user(1)
        val user2 = EntityDefaults.user(2)

        game.homeUser = user1
        game.awayUser = user2
        game.scoreHome = 3
        game.scoreAway = 2
        game.group = createGroup(user1, user2)

        Mockito
                .`when`(configurationService.parsedPointsPatternConfiguration)
                .thenReturn(listOf(intArrayOf(3, 3), intArrayOf(2, 1)))

        Mockito
                .`when`(groupStandingRepository.saveAll(Mockito.anyIterable<GroupStanding>() as MutableIterable<GroupStanding>))
                .thenAnswer { invocation ->
                    val actualStandings = invocation.getArgument<List<GroupStanding>>(0)
                    val winnerStandings = actualStandings[0]
                    val loserStandings = actualStandings[1]

                    Assert.assertEquals(user1, winnerStandings.user)
                    Assert.assertEquals(2, winnerStandings.games)
                    Assert.assertEquals(6, winnerStandings.points)
                    Assert.assertEquals(2, winnerStandings.gameRatio)
                    Assert.assertEquals(4, winnerStandings.roundRatio)

                    Assert.assertEquals(user2, loserStandings.user)
                    Assert.assertEquals(2, loserStandings.games)
                    Assert.assertEquals(4, loserStandings.points)
                    Assert.assertEquals(0, loserStandings.gameRatio)
                    Assert.assertEquals(2, loserStandings.roundRatio)

                    actualStandings.asIterable()
                }

        groupService.calcTableByGame(game)
    }

    private fun createGroup(user1: User, user2: User): Group {
        val group = Group()
        group.standings = listOf(createGroupStanding(user1, group), createGroupStanding(user2, group))
        return group
    }

    private fun createGroupStanding(user: User, group: Group): GroupStanding {
        val groupStanding = GroupStanding(group, user)
        groupStanding.user = user
        groupStanding.games = 1
        groupStanding.points = 3
        groupStanding.gameRatio = 1
        groupStanding.roundRatio = 3
        return groupStanding
    }
}
