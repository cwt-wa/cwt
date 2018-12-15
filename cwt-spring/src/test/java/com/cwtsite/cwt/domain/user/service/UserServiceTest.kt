package com.cwtsite.cwt.domain.user.service

import com.cwtsite.cwt.domain.application.service.ApplicationRepository
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.group.entity.Group
import com.cwtsite.cwt.domain.group.service.GroupRepository
import com.cwtsite.cwt.domain.playoffs.service.PlayoffService
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.entity.GroupStanding
import com.cwtsite.cwt.test.EntityDefaults
import org.assertj.core.api.Assertions
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class UserServiceTest {

    @InjectMocks private lateinit var userService: UserService
    @Mock private lateinit var userRepository: UserRepository
    @Mock private lateinit var authService: AuthService
    @Mock private lateinit var tournamentService: TournamentService
    @Mock private lateinit var tournamentRepository: TournamentRepository
    @Mock private lateinit var applicationRepository: ApplicationRepository
    @Mock private lateinit var groupRepository: GroupRepository
    @Mock private lateinit var playoffService: PlayoffService
    @Mock private lateinit var gameRepository: GameRepository

    @Test
    fun getRemainingOpponents() {
        val user = EntityDefaults.user(1)
        val tournament = EntityDefaults.tournament()
        tournament.status = TournamentStatus.GROUP

        Mockito
                .`when`(tournamentService.currentTournament)
                .thenReturn(tournament)

        val group = Group()
        group.standings = createStandings(group, user)

        Mockito
                .`when`(groupRepository.findByTournamentAndUser(Mockito.any(), Mockito.any()))
                .thenReturn(group)
        Mockito
                .`when`(gameRepository.findPlayedByUserInGroup(Mockito.any(), Mockito.any()))
                .thenReturn(createGames(group))

        Assertions
                .assertThat(userService.getRemainingOpponents(user))
                .containsExactlyInAnyOrder(getUser(group, 3), getUser(group, 4))
    }

    private fun createGames(group: Group): List<Game> {
        val game1 = Game()
        game1.group = group
        game1.id = LocalTime.now().toNanoOfDay()
        game1.homeUser = getUser(group, 1)
        game1.awayUser = getUser(group, 2)
        return listOf(game1)
    }

    private fun getUser(group: Group, userId: Long): User {
        return group.standings
                .map { it.user }
                .find { it.id == userId } ?: throw IllegalArgumentException()
    }

    private fun createStandings(group: Group, user: User): List<GroupStanding> {
        return Arrays.asList(
                GroupStanding(group, user),
                GroupStanding(group, EntityDefaults.user(2)),
                GroupStanding(group, EntityDefaults.user(3)),
                GroupStanding(group, EntityDefaults.user(4))
        )
    }

    @Test
    fun validateUsername() {
        Assert.assertFalse(userService.validateUsername("Strawberrycheesecake"))
        Assert.assertFalse(userService.validateUsername("Zemke`NNN"))
        Assert.assertFalse(userService.validateUsername("Zemke "))
        Assert.assertFalse(userService.validateUsername(" "))
        Assert.assertFalse(userService.validateUsername(""))
        Assert.assertTrue(userService.validateUsername("Z3mk3"))
        Assert.assertTrue(userService.validateUsername("Zemke"))
    }

    @Test
    fun validateEmail() {
        Assert.assertFalse(userService.validateEmail("@"))
        Assert.assertFalse(userService.validateEmail("asdsad"))
        Assert.assertFalse(userService.validateEmail("asdsad@"))
        Assert.assertFalse(userService.validateEmail("@asds ad"))
        Assert.assertFalse(userService.validateEmail("sad @cho"))
        Assert.assertFalse(userService.validateEmail("sad@cho "))
        Assert.assertFalse(userService.validateEmail(" sad@cho"))
        Assert.assertFalse(userService.validateEmail(" sad@cho"))
        Assert.assertTrue(userService.validateEmail("sad@cho"))
        Assert.assertTrue(userService.validateEmail("choc@choch.de"))
        Assert.assertTrue(userService.validateEmail("choc@choch.com"))
    }

    @Test
    fun createDefaultTimeline() {
        Mockito
                .`when`(tournamentRepository.findAll())
                .thenReturn(listOf(
                        EntityDefaults.tournament(id = 1, created = LocalDateTime.of(2002, 10, 1, 13, 32), maxRounds = 5),
                        EntityDefaults.tournament(id = 2, created = LocalDateTime.of(2003, 5, 13, 17, 32), maxRounds = 7),
                        EntityDefaults.tournament(id = 3, created = LocalDateTime.of(2004, 12, 1, 19, 32), maxRounds = 5)))

        Assert.assertEquals("[1,2002,5,0],[2,2003,7,0],[3,2004,5,0]", userService.createDefaultUserStatsTimeline())
    }
}
