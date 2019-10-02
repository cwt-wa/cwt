package com.cwtsite.cwt.domain.user.service

import com.cwtsite.cwt.core.EmailService
import com.cwtsite.cwt.core.FileValidator
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
import com.cwtsite.cwt.domain.user.repository.entity.Photo
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationAdapter
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.sql.Timestamp
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.security.auth.login.CredentialException

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
            private val countryRepository: CountryRepository,
            @Lazy private val emailService: EmailService) {

    @Value("\${reset-key-expiration-minutes}") private val resetKeyExpirationMinutes: Int? = null

    @Transactional
    @Throws(UserService.UserExistsByEmailOrUsernameException::class, UserService.InvalidUsernameException::class, UserService.InvalidEmailException::class)
    fun registerUser(username: String, email: String, password: String): User {
        val trimmedUsername = username.trim { it <= ' ' }
        val trimmedEmail = email.trim { it <= ' ' }

        if (!validateUsername(trimmedUsername)) throw InvalidUsernameException()
        if (!validateEmail(trimmedEmail)) throw InvalidEmailException()

        if (userRepository.findByEmailEqualsOrUsernameEquals(trimmedEmail, trimmedUsername).isPresent) {
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
                && !applicationRepository.findByApplicantAndTournament(user, currentTournament).isPresent)
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
                val group = groupRepository.findByTournamentAndUser(currentTournament, user) ?: return emptyList()
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

    @Throws(UserService.InvalidUsernameException::class, UsernameTakenException::class, InvalidEmailException::class, EmailExistsException::class)
    @Transactional
    fun changeUser(user: User, newAboutText: String? = null, newUsername: String? = null, newCountry: Country? = null,
                   newEmail: String? = null): User {
        if (newUsername != null) {
            if (validateUsername(newUsername)) user.username = newUsername
            else throw InvalidUsernameException()

            if (userRepository.findByUsernameIgnoreCase(newUsername).any { it != user }) throw UsernameTakenException()
        }

        if (newAboutText != null) user.about = newAboutText;
        if (newCountry != null) user.country = newCountry;
        if (newEmail != null) {
            val newEmailTrimmed = newEmail.trim { it <= ' ' }
            if (!validateEmail(newEmailTrimmed)) throw InvalidEmailException()
            if (userRepository.findByEmailIgnoreCase(newEmailTrimmed).any { it != user }) throw EmailExistsException()
            user.email = newEmailTrimmed
        }

        return userRepository.save(user)
    }

    @Throws(CredentialException::class)
    fun changePassword(user: User, currentPassword: String, newPassword: String) {
        val newPasswordHashed = authService.createHash(newPassword)
        if (user.password != authService.createHash(currentPassword)) throw CredentialException()
        userRepository.save(with(user) { password = newPasswordHashed; this })
    }

    @Transactional
    fun initiatePasswordReset(user: User) {
        user.resetKey = RandomStringUtils.random(20, true, true)
        user.resetDate = Timestamp.from(Instant.now())

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronizationAdapter() {
                override fun afterCommit() {
                    val message = """
            Hello ${user.username},
            
            you have requested to reset your password and you can do so by clicking the following link:
            
            http://cwtsite.com/password-reset?key=${user.resetKey}
            
            If you did not request this, please ignore this email.
            
            If you have questions, please do not answer to this email but rather contact our support via support@cwtsite.com.
            
            Kind regards
        """.trimIndent()

                    emailService.sendMail(
                            message, "Password reset", user.email,
                            EmailService.EMAIL_ADDRESS.NOREPLY)
                }
            })
        }
    }

    fun findAllOrderedByUsername(): List<User> = userRepository.findAll(Sort.by(Sort.Direction.ASC, "username"))

    fun validateUsername(username: String): Boolean = username.length <= 16 && username.matches("^[a-zA-Z0-9]+$".toRegex())

    fun validateEmail(email: String): Boolean = email.matches("^[^ ]+@[^ ]+$".toRegex())

    fun saveUser(user: User): User = userRepository.save(user)

    fun findByUsername(username: String): User? = userRepository.findByUsername(username)

    fun getById(id: Long): Optional<User> = userRepository.findById(id)

    fun getByIds(ids: List<Long>): List<User> = userRepository.findAllById(ids)

    fun findAll(): List<User> = userRepository.findAll()

    fun findByIds(vararg userId: Long): List<User> = userRepository.findAllById(userId.toList())

    fun findByUsernameContaining(term: String): List<User> = userRepository.findByUsernameContaining(term)

    fun findCountryById(countryId: Long) = countryRepository.findById(countryId)

    @Throws(FileValidator.UploadSecurityException::class, FileValidator.IllegalFileContentTypeException::class, FileValidator.FileEmptyException::class, FileValidator.FileTooLargeException::class, FileValidator.IllegalFileExtension::class)
    fun changePhoto(user: User, photo: MultipartFile) {
        FileValidator.validate(
                photo, 3000000,
                listOf("image/jpeg", "image/png", "image/gif"), // Yes, image/jpg is invalid.
                listOf("jpg", "jpeg", "png", "gif"))

        user.photo = Photo(file = photo.bytes, mediaType = photo.contentType, extension = StringUtils.getFilenameExtension(photo.originalFilename))
        userRepository.save(user)
    }

    fun findByUsernames(usernames: List<String>): List<User> = userRepository.findByUsernameIn(usernames)

    fun findByUsernamesIgnoreCase(usernames: List<String>): List<User> = userRepository.findByUsernameLowercaseIn(usernames.map {it.toLowerCase()})

    fun findByEmail(email: String): User? {
        val users = userRepository.findByEmailIgnoreCase(email)
        if (users.isEmpty()) return null
        else if (users.size > 1) throw IllegalStateException()
        return users[0]
    }

    /**
     * @throws IllegalArgumentException The reset key is invalid.
     * @throws IllegalStateException The reset key has expired.
     */
    @Transactional
    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun executePasswordReset(user: User, resetKey: String, password: String): User {
        if (user.resetKey != resetKey)
            throw IllegalArgumentException("The reset key is wrong.")

        if (Duration.between(Instant.now(), user.resetDate!!.toInstant()).toMinutes() > resetKeyExpirationMinutes!!)
            throw IllegalStateException("The reset key has expired.")

        return with(user) {
            this.password = authService.createHash(password)
            this.resetKey = null
            this.resetDate = null
            this
        }
    }

    fun findByResetKey(resetKey: String): User? = userRepository.findByResetKey(resetKey)

    inner class UserExistsByEmailOrUsernameException : RuntimeException()

    inner class InvalidUsernameException : RuntimeException()

    inner class UsernameTakenException : RuntimeException()

    inner class EmailExistsException : RuntimeException()

    inner class InvalidEmailException : RuntimeException()
}
