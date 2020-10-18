package com.cwtsite.cwt.domain.user.service

import com.cwtsite.cwt.core.EmailService
import com.cwtsite.cwt.domain.application.service.ApplicationRepository
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.group.entity.Group
import com.cwtsite.cwt.domain.group.service.GroupRepository
import com.cwtsite.cwt.domain.playoffs.service.PlayoffService
import com.cwtsite.cwt.domain.playoffs.service.TreeService
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.CountryRepository
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.Country
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.entity.GroupStanding
import com.cwtsite.cwt.test.EntityDefaults
import com.cwtsite.cwt.test.MockitoUtils
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
    @Mock private lateinit var countryRepository: CountryRepository
    @Mock private lateinit var emailService: EmailService
    @Mock private lateinit var treeService: TreeService

    @Test
    fun getRemainingOpponents() {
        val user = EntityDefaults.user(1)
        val tournament = EntityDefaults.tournament(status = TournamentStatus.GROUP)

        Mockito
                .`when`(tournamentService.getCurrentTournament())
                .thenReturn(tournament)

        val group = Group()
        group.standings.addAll(createStandings(user))

        Mockito
                .`when`(groupRepository.findByTournamentAndUser(Mockito.any(), Mockito.any()))
                .thenReturn(group)
        Mockito
                .`when`(gameRepository.findPlayedByUserInGroup(Mockito.any(), Mockito.any()))
                .thenReturn(createGames(group, tournament))

        Assertions
                .assertThat(userService.getRemainingOpponents(user))
                .containsExactlyInAnyOrder(getUser(group, 3), getUser(group, 4))
    }

    private fun createGames(group: Group, tournament: Tournament): List<Game> {
        val game = Game(tournament = tournament)
        game.group = group
        game.id = LocalTime.now().toNanoOfDay()
        game.homeUser = getUser(group, 1)
        game.awayUser = getUser(group, 2)
        return listOf(game)
    }

    private fun getUser(group: Group, userId: Long): User {
        return group.standings
                .map { it.user }
                .find { it.id == userId } ?: throw IllegalArgumentException()
    }

    private fun createStandings(user: User): List<GroupStanding> =
            listOf(
                    GroupStanding(user),
                    GroupStanding(EntityDefaults.user(2)),
                    GroupStanding(EntityDefaults.user(3)),
                    GroupStanding(EntityDefaults.user(4)))

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
                        EntityDefaults.tournament(id = 1, created = LocalDateTime.of(2002, 10, 1, 13, 32), maxRounds = 5, threeWay = true),
                        EntityDefaults.tournament(id = 2, created = LocalDateTime.of(2003, 5, 13, 17, 32), maxRounds = 7, threeWay = false),
                        EntityDefaults.tournament(id = 3, created = LocalDateTime.of(2004, 12, 1, 19, 32), maxRounds = 5, threeWay = false)))

        Assert.assertEquals("[1,2002,1,5,0],[2,2003,0,7,0],[3,2004,0,5,0]", userService.createDefaultUserStatsTimeline())
    }

    @Test
    fun changeUser_aboutText() {
        val user = EntityDefaults.user()
        user.about = "hello i am an about text"

        Mockito
                .`when`<Any>(userRepository.save(MockitoUtils.anyObject()))
                .thenAnswer { it.getArgument(0) }

        val newUser = userService.changeUser(user, "hello i am not the same about text!", null, null)
        Assert.assertEquals("hello i am not the same about text!", newUser.about)
    }

    @Test
    fun changeUser_username() {
        val user = EntityDefaults.user()
        user.username = "leasOldName"

        Mockito
                .`when`<Any>(userRepository.save(MockitoUtils.anyObject()))
                .thenAnswer { it.getArgument(0) }

        val newUser = userService.changeUser(user, null, "leasNewName", null)
        Assert.assertEquals("leasNewName", newUser.username)
    }

    @Test
    fun changeUser_usernameInvalid() {
        val user = EntityDefaults.user()
        user.username = "leasOldName"

        try {
            userService.changeUser(user, null, "X7s///st", null)
            Assert.fail("Was not validating username.")
        } catch (e: UserService.InvalidUsernameException) {
        }
    }

    @Test
    fun changeUser_email() {
        val user = EntityDefaults.user()
        user.email = "lea@flori"

        Mockito
                .`when`<Any>(userRepository.save(MockitoUtils.anyObject()))
                .thenAnswer { it.getArgument(0) }

        val newUser = userService.changeUser(user, null, null, null, "flori@lea")
        Assert.assertEquals("flori@lea", newUser.email)
    }

    @Test
    fun changeUser_country() {
        val user = EntityDefaults.user()
        user.about = "england"

        Mockito
                .`when`<Any>(userRepository.save(MockitoUtils.anyObject()))
                .thenAnswer { it.getArgument(0) }

        val newUser = userService.changeUser(user, null, null, createCountry("Germany"))
        Assert.assertEquals("Germany", newUser.country.name)
    }

    @Test
    fun changeUser_complete() {
        val user = EntityDefaults.user()
        user.about = "old about text"
        user.country = createCountry("England")
        user.username = "oldUsername"
        user.email = "old@email"

        Mockito
                .`when`<Any>(userRepository.save(MockitoUtils.anyObject()))
                .thenAnswer { it.getArgument(0) }

        val newUser = userService.changeUser(user, "new about text", "newUsernameXoXo", createCountry("Germany"), "new@email")
        Assert.assertEquals("new about text", newUser.about)
        Assert.assertEquals("newUsernameXoXo", newUser.username)
        Assert.assertEquals("Germany", newUser.country.name)
        Assert.assertEquals("new@email", newUser.email)
    }

    private fun createCountry(name: String) = Country(
            id = 1,
            name = name,
            flag = "${name.toLowerCase().replace(" ", "_")}.png"
    )
}
