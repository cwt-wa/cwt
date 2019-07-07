package com.cwtsite.cwt.domain.user.service

import com.cwtsite.cwt.core.toInt
import com.cwtsite.cwt.domain.application.service.ApplicationRepository
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.group.service.GroupRepository
import com.cwtsite.cwt.domain.playoffs.service.PlayoffService
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.CountryRepository
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.Country
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class UserService @Autowired
constructor(private val userRepository: UserRepository,
            private val authService: AuthService,
            private val tournamentService: TournamentService,
            private val tournamentRepository: TournamentRepository,
            private val applicationRepository: ApplicationRepository,
            private val groupRepository: GroupRepository,
            private val playoffService: PlayoffService,
            private val gameRepository: GameRepository,
            private val countryRepository: CountryRepository) {

    @Transactional
    @Throws(UserService.UserExistsByEmailOrUsernameException::class, UserService.InvalidUsernameException::class, UserService.InvalidEmailException::class)
    fun registerUser(username: String, email: String, password: String): User {
        val trimmedUsername = username.trim { it <= ' ' }
        val trimmedEmail = email.trim { it <= ' ' }

        if (!validateUsername(trimmedUsername)) throw InvalidUsernameException()
        if (!validateEmail(trimmedEmail)) throw InvalidEmailException()

        if (userRepository.findByEmailEqualsOrUsernameEquals(trimmedEmail, trimmedUsername) != null) {
            throw UserExistsByEmailOrUsernameException()
        }
        return userRepository.save(User(
                username = trimmedUsername,
                email = trimmedEmail,
                password = authService.createHash(password)))
    }

    fun userCanApplyForCurrentTournament(user: User): Boolean {
        val currentTournament = tournamentService.getCurrentTournament()

        return (TournamentStatus.OPEN == currentTournament.status
                && applicationRepository.findByApplicantAndTournament(user, currentTournament) == null)
    }

    fun userCanReportForCurrentTournament(user: User): Boolean {
        val userCanReportForCurrentTournament: Boolean
        val currentTournament = try {
            tournamentService.getCurrentTournament()
        } catch (e: RuntimeException) {
            return false
        }

        if (currentTournament.status == TournamentStatus.GROUP) {
            val group = this.groupRepository.findByTournamentAndUser(currentTournament, user)

            if (group == null) {
                userCanReportForCurrentTournament = false
            } else {
                val numberOfGamesAlreadyPlayed = group.standings
                        .find { it.user == user }
                        ?.games ?: throw IllegalArgumentException()
                val numberOfTotalGamesToPlay = group.standings.size - 1
                userCanReportForCurrentTournament = numberOfTotalGamesToPlay > numberOfGamesAlreadyPlayed
            }
        } else if (currentTournament.status == TournamentStatus.PLAYOFFS) {
            userCanReportForCurrentTournament = playoffService.getNextGameForUser(user) != null
        } else {
            userCanReportForCurrentTournament = false
        }

        return userCanReportForCurrentTournament
    }

    fun getRemainingOpponents(user: User): List<User> {
        val currentTournament = tournamentService.getCurrentTournament()
        val remainingOpponents: List<User>

        when (currentTournament.status) {
            TournamentStatus.GROUP -> {
                val group = groupRepository.findByTournamentAndUser(currentTournament, user)
                val games = gameRepository.findPlayedByUserInGroup(user, group)

                remainingOpponents = group.standings
                        .filter { groupStanding -> groupStanding.user != user }
                        .map { it.user }
                        .filter { u ->
                            !games
                                    .flatMap { g -> listOf(g.homeUser, g.awayUser) }
                                    .distinct()
                                    .contains(u)
                        }
                        .filter { u -> u != user }
            }
            TournamentStatus.PLAYOFFS -> {
                val nextPlayoffGameForUser = playoffService.getNextGameForUser(user)

                remainingOpponents = if (nextPlayoffGameForUser == null) emptyList() else {
                    if (nextPlayoffGameForUser.homeUser == user) listOf(nextPlayoffGameForUser.awayUser!!)
                    else listOf(nextPlayoffGameForUser.homeUser!!)
                }
            }
            else -> remainingOpponents = emptyList()
        }

        return remainingOpponents
    }

    fun createDefaultUserStatsTimeline(): String = tournamentRepository.findAll()
            .joinToString(separator = ",") { "[${it.id},${it.created!!.toLocalDateTime().year},${it.threeWay?.toInt() ?: 0},${it.maxRounds},0]" }

    fun findPaginated(page: Int, size: Int, sort: Sort): Page<User> {
        var extendedSort = sort
        if (extendedSort == Sort.by(Sort.Direction.DESC, "userStats.trophyPoints")
                || extendedSort == Sort.by(Sort.Direction.ASC, "userStats.trophyPoints")) {
            extendedSort = extendedSort.and(Sort.by(Sort.Direction.DESC, "userStats.participations"))
        }
        extendedSort = extendedSort.and(Sort.by(Sort.Direction.ASC, "username"))
        return userRepository.findAll(PageRequest.of(page, size, extendedSort))
    }

    @Throws(UserService.InvalidUsernameException::class)
    fun changeUser(user: User, newAboutText: String? = null, newUsername: String? = null, newCountry: Country? = null): User {
        if (newUsername != null) {
            if (validateUsername(newUsername)) user.username = newUsername
            else throw InvalidUsernameException()
        }

        if (newAboutText != null) user.about = newAboutText;
        if (newCountry != null) user.country = newCountry;

        return userRepository.save(user)
    }

    fun findAllOrderedByUsername(): List<User> = userRepository.findAll(Sort.by(Sort.Direction.ASC, "username"))

    fun validateUsername(username: String): Boolean = username.length <= 16 && username.matches("^[a-zA-Z0-9]+$".toRegex())

    fun validateEmail(email: String): Boolean = email.matches("^[^ ]+@[^ ]+$".toRegex())

    fun saveUser(user: User): User = userRepository.save(user)

    fun findByUsername(username: String): User = userRepository.findByUsername(username)

    fun getById(id: Long): Optional<User> = userRepository.findById(id)

    fun getByIds(ids: List<Long>): List<User> = userRepository.findAllById(ids)

    fun findAll(): List<User> = userRepository.findAll()

    fun findByIds(vararg userId: Long): List<User> = userRepository.findAllById(userId.toList())

    fun findByUsernameContaining(term: String): List<User> = userRepository.findByUsernameContaining(term)

    fun findCountryById(countryId: Long) = countryRepository.findById(countryId)

    inner class UserExistsByEmailOrUsernameException : RuntimeException()

    inner class InvalidUsernameException : RuntimeException()

    inner class InvalidEmailException : RuntimeException()
}
