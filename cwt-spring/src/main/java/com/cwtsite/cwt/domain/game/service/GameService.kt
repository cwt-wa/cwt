package com.cwtsite.cwt.domain.game.service

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.core.FileValidator
import com.cwtsite.cwt.domain.bet.entity.Bet
import com.cwtsite.cwt.domain.bet.service.BetRepository
import com.cwtsite.cwt.domain.configuration.entity.Configuration
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.configuration.service.ConfigurationService
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.GameStats
import com.cwtsite.cwt.domain.game.entity.Rating
import com.cwtsite.cwt.domain.game.entity.Replay
import com.cwtsite.cwt.domain.game.entity.enumeration.RatingType
import com.cwtsite.cwt.domain.group.service.GroupRepository
import com.cwtsite.cwt.domain.group.service.GroupService
import com.cwtsite.cwt.domain.playoffs.service.PlayoffService
import com.cwtsite.cwt.domain.playoffs.service.TreeService
import com.cwtsite.cwt.domain.schedule.service.ScheduleService
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.exception.IllegalTournamentStatusException
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.domain.user.service.UserService
import com.cwtsite.cwt.entity.Comment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationAdapter
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

@Component
class GameService @Autowired
constructor(private val gameRepository: GameRepository, private val tournamentService: TournamentService, private val groupRepository: GroupRepository,
            private val userRepository: UserRepository, private val groupService: GroupService, private val ratingRepository: RatingRepository,
            private val commentRepository: CommentRepository, private val configurationService: ConfigurationService, private val userService: UserService,
            private val playoffService: PlayoffService, private val betRepository: BetRepository, private val scheduleService: ScheduleService,
            private val treeService: TreeService, private val gameStatsRepository: GameStatsRepository) {

    @Transactional
    @Throws(InvalidOpponentException::class, InvalidScoreException::class, IllegalTournamentStatusException::class, FileValidator.UploadSecurityException::class, FileValidator.IllegalFileContentTypeException::class, FileValidator.FileEmptyException::class, FileValidator.FileTooLargeException::class, FileValidator.IllegalFileExtension::class, IOException::class)
    fun reportGame(homeUser: Long, awayUser: Long, scoreHome: Int, scoreAway: Int, replay: MultipartFile): Game {
        FileValidator.validate(
            replay, 150000,
            listOf("application/octet-stream", "application/zip", "application/x-zip-compressed"),
            listOf("zip"))
        val reportedGame = reportGame(homeUser, awayUser, scoreHome, scoreAway, false)
        reportedGame.replay = Replay(replay.bytes, replay.contentType, StringUtils.getFilenameExtension(replay.originalFilename))
        return gameRepository.save(reportedGame)
    }

    fun createReplayFileName(game: Game): String {
        return String.format(
                "%s_%s_%s-%s_%s.%s",
                game.id,
                game.homeUser!!.username.replace("[^a-zA-Z0-9-_\\\\.]".toRegex(), "_"),
                game.scoreHome, game.scoreAway,
                game.awayUser!!.username.replace("[^a-zA-Z0-9-_\\\\.]".toRegex(), "_"),
                game.replay!!.extension)
    }

    @Transactional
    @Throws(InvalidOpponentException::class, InvalidScoreException::class, IllegalTournamentStatusException::class)
    fun reportGame(homeUserId: Long, awayUserId: Long, homeScore: Int, awayScore: Int, persist: Boolean = true): Game {
        val currentTournament = tournamentService.getCurrentTournament()
                ?: throw RestException("There's no tournament currently.", HttpStatus.BAD_REQUEST, null)
        val bestOfValue = Integer.valueOf(getBestOfValue(currentTournament.status).value)
        val winnerScore = ceil(bestOfValue.toDouble() / 2)

        if (homeScore.toDouble() != winnerScore && awayScore.toDouble() != winnerScore || homeScore + awayScore > bestOfValue) {
            throw InvalidScoreException(String.format(
                    "Score %s-%s should have been best of %s.",
                    homeScore, awayScore, bestOfValue))
        } else if (homeScore < 0 || awayScore < 0) {
            throw InvalidScoreException(String.format(
                    "Score %s-%s should not include negative scores.", homeScore, awayScore))
        }

        val homeUser = userRepository.findById(homeUserId).orElseThrow<RuntimeException> { throw RuntimeException() }
        val remainingOpponents = userService.getRemainingOpponents(homeUser)
        val awayUser = userRepository.findById(awayUserId).orElseThrow<RuntimeException> { throw RuntimeException() }

        if (!remainingOpponents.contains(awayUser)) {
            throw InvalidOpponentException(String.format(
                    "Opponent %s is not in %s",
                    awayUser.id, remainingOpponents.map { it.id }))
        }

        val reportedGame: Game

        if (currentTournament.status == TournamentStatus.GROUP) {
            val group = groupRepository.findByTournamentAndUser(currentTournament, awayUser)

            val game = Game(tournament = currentTournament)

            game.scoreHome = homeScore
            game.scoreAway = awayScore
            game.tournament = currentTournament
            game.homeUser = homeUser
            game.awayUser = awayUser
            game.reporter = homeUser

            game.group = group

            groupService.calcTableByGame(game)
            reportedGame = if (persist) gameRepository.save(game) else game
        } else if (currentTournament.status == TournamentStatus.PLAYOFFS) {
            val playoffGameToBeReported = gameRepository.findNextPlayoffGameForUser(currentTournament, homeUser)

            if (!Arrays.asList(playoffGameToBeReported.homeUser, playoffGameToBeReported.awayUser)
                            .containsAll(Arrays.asList(homeUser, awayUser))) {
                throw InvalidOpponentException(String.format(
                        "Next playoff game is expected to be %s vs. %s.",
                        homeUser.username, awayUser.username))
            }

            playoffGameToBeReported.reporter = homeUser

            if (playoffGameToBeReported.homeUser == homeUser) {
                playoffGameToBeReported.scoreHome = homeScore
                playoffGameToBeReported.scoreAway = awayScore
            } else {
                playoffGameToBeReported.scoreHome = awayScore
                playoffGameToBeReported.scoreAway = homeScore
            }

            reportedGame = gameRepository.save(playoffGameToBeReported)
            playoffService.advanceByGame(reportedGame)
        } else {
            throw IllegalTournamentStatusException(TournamentStatus.GROUP, TournamentStatus.PLAYOFFS)
        }

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronizationAdapter() {
                override fun afterCommit() {
                    GlobalScope.launch {
                        scheduleService.delete(
                                scheduleService.findByPairing(reportedGame.homeUser!!, reportedGame.awayUser!!) ?: return@launch)
                    }
                }
            })
        }

        reportedGame.reportedAt = Timestamp(System.currentTimeMillis())
        return reportedGame
    }

    fun getBestOfValue(tournamentStatus: TournamentStatus): Configuration {
        val configurationKey: ConfigurationKey

        if (tournamentStatus == TournamentStatus.GROUP) {
            configurationKey = ConfigurationKey.GROUP_GAMES_BEST_OF
        } else if (tournamentStatus == TournamentStatus.PLAYOFFS) {
            configurationKey = if (treeService.onlyFinalGamesAreLeftToPlay())
                ConfigurationKey.FINAL_GAME_BEST_OF
            else
                ConfigurationKey.PLAYOFF_GAMES_BEST_OF
        } else {
            throw IllegalTournamentStatusException(TournamentStatus.GROUP, TournamentStatus.PLAYOFFS)
        }

        return configurationService.getOne(configurationKey)
    }

    @Transactional
    @Throws(PlayoffService.PlayoffGameNotVoidableException::class, IllegalStateException::class)
    fun voidGame(game: Game): Game {
        val currentTournament = tournamentService.getCurrentTournament()

        if (currentTournament == null || game.tournament != currentTournament)
            throw IllegalStateException("Can only void games of current tournament.")

        game.voided = true

        return when (game.tournament.status) {
            TournamentStatus.GROUP -> groupService.reverseStandingsByGame(game)
            TournamentStatus.PLAYOFFS -> playoffService.voidPlayoffGame(game)
            else -> throw IllegalStateException("Game status must be either group or playoff.")
        }
    }

    fun saveAll(games: List<Game>): List<Game> {
        return gameRepository.saveAll(games)
    }

    fun findById(id: Long): Optional<Game> {
        return gameRepository.findById(id)
    }

    fun rateGame(gameId: Long, userId: Long, type: RatingType): Rating {
        val user = userRepository.findById(userId)
                .orElseThrow<IllegalArgumentException> { throw IllegalArgumentException() }
        val game = gameRepository.findById(gameId)
                .orElseThrow<IllegalArgumentException> { throw IllegalArgumentException() }
        return ratingRepository.save(Rating(type, user, game))
    }

    fun commentGame(gameId: Long, userId: Long, body: String): Comment {
        val user = userRepository.findById(userId)
                .orElseThrow<IllegalArgumentException> { throw IllegalArgumentException() }
        val game = gameRepository.findById(gameId)
                .orElseThrow<IllegalArgumentException> { throw IllegalArgumentException() }
        return commentRepository.save(Comment(body, user, game))
    }

    fun findPaginated(page: Int, size: Int, sort: Sort): Page<Game> =
            gameRepository.findAll(PageRequest.of(page, size, sort))

    fun findPaginatedPlayedGames(page: Int, size: Int, sort: Sort): Page<Game> =
            gameRepository.findByHomeUserNotNullAndAwayUserNotNullAndScoreHomeNotNullAndScoreAwayNotNull(
                    PageRequest.of(page, size, sort))

    /**
     * @throws IllegalStateException There's no current tournament.
     */
    @Throws(IllegalStateException::class)
    @Transactional
    fun addTechWin(winner: User, loser: User): Game {
        val currentTournament = tournamentService.getCurrentTournament()
                ?: throw IllegalStateException("There's no current tournament.")
        return reportGame(
                winner.id!!, loser.id!!,
                ceil(getBestOfValue(currentTournament.status).value!!.toDouble() / 2).toInt(), 0)
    }

    fun placeBet(game: Game, user: User, betOnHome: Boolean): Bet {
        val bet = betRepository.findByUserAndGame(user, game)
                .map { with(it) { it.betOnHome = betOnHome; it } }
                .orElse(Bet(user = user, game = game, betOnHome = betOnHome))
        return betRepository.save(bet)
    }

    fun saveGameStats(data: String, game: Game): GameStats {
        val startedAt = JSONObject(data).getString("startedAt") // Formatting example: 2013-12-14 16:45:20 GMT
        val map = JSONObject(data).optString("map", null)
        val format = with (SimpleDateFormat("yyyy-mm-dd HH:mm:ss z")) {
            timeZone = TimeZone.getTimeZone("GMT")
            this
        }
        return gameStatsRepository.save(GameStats(
                data = data,
                startedAt = Timestamp(format.parse(startedAt).time),
                map = map,
                game = game
        ))
    }

    fun findGameStats(game: Game): String =
            gameStatsRepository.findByGame(game)
                    .sortedBy { it.startedAt }
                    .joinToString(prefix = "[", postfix = "]") { it.data }

    fun updateReplayQuantity(game: Game, replayQuantity: Int): Game {
        game.replayQuantity = replayQuantity
        return gameRepository.save(game)
    }

    fun findBetsByGame(game: Game): List<Bet> = betRepository.findByGame(game)

    fun findGroupGamesInclVoided(tournament: Tournament): List<Game> = gameRepository.findByGroupNotNullAndTournament(tournament)

    fun findGroupGames(tournament: Tournament): List<Game> = gameRepository.findByGroupNotNullAndVoidedFalseAndTournament(tournament)

    inner class InvalidScoreException internal constructor(message: String) : RuntimeException(message)

    inner class InvalidOpponentException internal constructor(message: String) : RuntimeException(message)
}

