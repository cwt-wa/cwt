package com.cwtsite.cwt.domain.game.service

import com.cwtsite.cwt.controller.RestException
import com.cwtsite.cwt.core.news.PublishNews
import com.cwtsite.cwt.domain.bet.entity.Bet
import com.cwtsite.cwt.domain.bet.service.BetRepository
import com.cwtsite.cwt.domain.configuration.entity.Configuration
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.configuration.service.ConfigurationService
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.GameStats
import com.cwtsite.cwt.domain.game.entity.Rating
import com.cwtsite.cwt.domain.game.entity.enumeration.RatingType
import com.cwtsite.cwt.domain.group.service.GroupRepository
import com.cwtsite.cwt.domain.group.service.GroupService
import com.cwtsite.cwt.domain.message.service.MessageNewsType
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
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

@Component
class GameService

@Autowired
constructor(private val gameRepository: GameRepository,
            private val tournamentService: TournamentService,
            private val groupRepository: GroupRepository,
            private val userRepository: UserRepository,
            private val groupService: GroupService,
            private val ratingRepository: RatingRepository,
            private val commentRepository: CommentRepository,
            private val configurationService: ConfigurationService,
            private val userService: UserService,
            private val playoffService: PlayoffService,
            private val betRepository: BetRepository,
            private val scheduleService: ScheduleService,
            private val treeService: TreeService,
            private val gameStatsRepository: GameStatsRepository) {

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
    @PublishNews
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

            if (!listOf(playoffGameToBeReported.homeUser, playoffGameToBeReported.awayUser)
                            .containsAll(listOf(homeUser, awayUser))) {
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

        GlobalScope.launch {
            scheduleService.delete(
                    scheduleService.findByPairing(reportedGame.homeUser!!, reportedGame.awayUser!!) ?: return@launch)
        }
        reportedGame.reportedAt = Timestamp(System.currentTimeMillis())
        return reportedGame
    }

    fun getBestOfValue(tournamentStatus: TournamentStatus): Configuration {
        val configurationKey = if (tournamentStatus == TournamentStatus.GROUP) {
            ConfigurationKey.GROUP_GAMES_BEST_OF
        } else if (tournamentStatus == TournamentStatus.PLAYOFFS) {
            if (treeService.onlyFinalGamesAreLeftToPlay())
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
    @PublishNews
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

    fun findById(id: Long): Optional<Game> {
        return gameRepository.findById(id)
    }

    @PublishNews
    fun rateGame(gameId: Long, userId: Long, type: RatingType): Rating {
        val user = userRepository.findById(userId)
                .orElseThrow<IllegalArgumentException> { throw IllegalArgumentException() }
        val game = gameRepository.findById(gameId)
                .orElseThrow<IllegalArgumentException> { throw IllegalArgumentException() }
        return ratingRepository.save(Rating(type, user, game))
    }

    @PublishNews
    fun commentGame(gameId: Long, userId: Long, body: String): Comment {
        val user = userRepository.findById(userId)
                .orElseThrow<IllegalArgumentException> { throw IllegalArgumentException() }
        val game = gameRepository.findById(gameId)
                .orElseThrow<IllegalArgumentException> { throw IllegalArgumentException() }
        return commentRepository.save(Comment(body, user, game))
    }

    fun findPaginatedPlayedGames(page: Int, size: Int, sort: Sort): Page<Game> =
            gameRepository.findByHomeUserNotNullAndAwayUserNotNullAndScoreHomeNotNullAndScoreAwayNotNull(
                    PageRequest.of(page, size, sort))

    fun findGameOfUsers(page: Int, size: Int, sort: Sort, user1: User, user2: User): Page<Game> =
            gameRepository.findGameOfUsers(PageRequest.of(page, size, sort), user1, user2);

    fun findGameOfUser(page: Int, size: Int, sort: Sort, user: User): Page<Game> {
        return gameRepository.findGameOfUser(PageRequest.of(page, size, sort), user)
    }

    fun findAllOfTournament(tournament: Tournament): List<Game> =
            gameRepository.findByTournament(tournament)

    /**
     * @throws IllegalStateException There's no current tournament.
     */
    @Throws(IllegalStateException::class)
    @Transactional
    @PublishNews
    fun addTechWin(winner: User, loser: User, reporter: User): Game {
        val currentTournament = tournamentService.getCurrentTournament()
                ?: throw IllegalStateException("There's no current tournament.")
        val game = reportGame(
                winner.id!!, loser.id!!,
                ceil(getBestOfValue(currentTournament.status).value!!.toDouble() / 2).toInt(), 0)
        game.techWin = true
        game.reporter = reporter
        return game
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
        val texture = JSONObject(data).optString("texture", null)
        val format = with (SimpleDateFormat("yyyy-MM-dd HH:mm:ss z")) {
            timeZone = TimeZone.getTimeZone("UTC")
            this
        }
        return gameStatsRepository.save(GameStats(
                data = data,
                startedAt = Timestamp(format.parse(startedAt).time),
                map = map,
                texture = texture,
                game = game
        ))
    }

    fun findMaps(page: Int, size: Int, texture: String?): Page<GameStats> {
        val pageRequest  = PageRequest.of(page, size, Sort.Direction.DESC, "created")
        return when (texture) {
            null -> gameStatsRepository.findByMapIsNotNullAndTextureIsNotNull(pageRequest)
            else -> gameStatsRepository.findByMapIsNotNullAndTextureEquals(texture, pageRequest)
        }
    }


    fun findGameStats(page: Int, size: Int): Page<GameStats> =
            gameStatsRepository.findAll(PageRequest.of(page, size, Sort.Direction.DESC, "created"))

    fun findGameStats(game: Game?): String =
            (game?.let { gameStatsRepository.findAllByGame(it) } ?: gameStatsRepository.findAll())
                    .sortedBy { it.startedAt }
                    .joinToString(prefix = "[", postfix = "]") { it.data }

    fun retrieveDistinctTextures(): List<String?> =
            gameStatsRepository.findDistinctByTextureAndMapIsNotNullAndTextureIsNotNull()

    fun findFromGameStats(game: Game?, vararg fields: String): List<Map<String, Any?>> {
        val result = mutableListOf<Map<String, Any?>>()
        val stats = JSONArray(findGameStats(game))
        for (i in 0 until stats.length()) {
            val map = mutableMapOf<String, Any?>()
            fields.forEach { field -> map[field] = stats.getJSONObject(i).opt(field) }
            result.add(map)
        }
        return result
    }

    fun updateReplayQuantity(game: Game, replayQuantity: Int): Game {
        game.replayQuantity = replayQuantity
        return gameRepository.save(game)
    }

    fun countTextures(): Map<String, Long> {
        return gameStatsRepository.findDistinctByTextureAndMapIsNotNullAndTextureIsNotNull()
                .map { texture -> (texture ?: "Unknown") to gameStatsRepository.countByTexture(texture) }
                .toMap()
    }

    fun findBetsByGame(game: Game): List<Bet> = betRepository.findAllByGame(game)

    fun findGroupGames(tournament: Tournament): List<Game> = gameRepository.findByGroupNotNullAndVoidedFalseAndTournament(tournament)

    inner class InvalidScoreException internal constructor(message: String) : RuntimeException(message)

    inner class InvalidOpponentException internal constructor(message: String) : RuntimeException(message)
}

