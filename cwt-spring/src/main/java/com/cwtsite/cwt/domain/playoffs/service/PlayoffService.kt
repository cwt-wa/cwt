package com.cwtsite.cwt.domain.playoffs.service

import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.configuration.service.ConfigurationService
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.PlayoffGame
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.group.service.GroupRepository
import com.cwtsite.cwt.domain.playoffs.ThreeWayFinalResult
import com.cwtsite.cwt.domain.playoffs.TiedThreeWayFinalResult
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.math.log2

@Component
class PlayoffService {

    @Autowired
    private lateinit var gameRepository: GameRepository

    @Autowired
    private lateinit var tournamentService: TournamentService

    @Autowired
    private lateinit var groupRepository: GroupRepository

    @Autowired
    private lateinit var configurationService: ConfigurationService

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getGamesOfTournament(tournament: Tournament): List<Game> {
        return gameRepository.findByTournamentAndPlayoffIsNotNull(tournament)
    }

    fun getVoidableGames(): List<Game> {
        return gameRepository.findByTournamentAndPlayoffIsNotNull(tournamentService.getCurrentTournament())
                .filter {
                    if (!it.wasPlayed()) return@filter false
                    if (isSomeKindOfFinalGame(it)) return@filter true
                    val (nextRound, nextSpot) = determineNextPlayoffSpot(it.playoff!!.round, it.playoff!!.spot)
                    val gameToAdvanceTo = gameRepository.findGameInPlayoffTree(it.tournament, nextRound, nextSpot)

                    if (!gameToAdvanceTo.isPresent) {
                        this.logger.warn("Playoff game ${it.id} has been played but a subsequent game has not been found in the playoff tree.")
                        return@filter true
                    }

                    if (isThreeWayFinalGame(gameToAdvanceTo.get().tournament, gameToAdvanceTo.get().playoff!!.round)) {
                        return@filter true
                    }

                    return@filter !gameToAdvanceTo.get().wasPlayed()
                }
    }

    private fun isSomeKindOfFinalGame(game: Game): Boolean =
            isFinalGame(game.tournament, game.playoff!!.round)
                    || isThirdPlaceGame(game.tournament, game.playoff!!.round)
                    || isThreeWayFinalGame(game.tournament, game.playoff!!.round)

    fun getNextGameForUser(user: User): Game? {
        return gameRepository.findNextPlayoffGameForUser(tournamentService.getCurrentTournament(), user)
    }

    fun onlyFinalGamesAreLeftToPlay(): Boolean {
        val currentTournament = tournamentService.getCurrentTournament()

        if (currentTournament.status != TournamentStatus.PLAYOFFS) {
            return false
        }

        val finalGames = gameRepository.findReadyGamesInRoundEqualOrGreaterThan(
                getNumberOfPlayoffRoundsInTournament(currentTournament))

        return finalGames.size == 2
    }

    private fun isFinalGame(tournament: Tournament, round: Int): Boolean {
        return round == getNumberOfPlayoffRoundsInTournament(tournament) + 1 && !isThreeWayFinalGame(tournament, round)
    }

    private fun isThirdPlaceGame(tournament: Tournament, round: Int): Boolean {
        return round == getNumberOfPlayoffRoundsInTournament(tournament) && !isThreeWayFinalGame(tournament, round)
    }

    private fun isThreeWayFinalGame(tournament: Tournament, round: Int): Boolean {
        val playersInFirstRound = gameRepository.findByTournamentAndRoundAndNotVoided(tournament, 1).size * 2
        val isPlayoffTreeWithThreeWayFinal = isPlayoffTreeWithThreeWayFinal(playersInFirstRound)
        if (!isPlayoffTreeWithThreeWayFinal) return false
        val numOfRounds = (Math.log((playersInFirstRound).toDouble()) / Math.log(2.0))
        return Math.floor(numOfRounds) == round.toDouble()
    }

    fun isPlayoffTreeWithThreeWayFinal(tournament: Tournament) =
            isPlayoffTreeWithThreeWayFinal(gameRepository.findByTournamentAndRoundAndNotVoided(tournament, 1).size * 2)

    private fun isPlayoffTreeWithThreeWayFinal(playersInFirstRound: Int) = log2(playersInFirstRound.toDouble()) % 1 != 0.0

    fun getNumberOfPlayoffRoundsInTournament(tournament: Tournament): Int {
        val groupsCount = groupRepository.countByTournament(tournament)
        val numberOfGroupMembersAdvancing = Integer.parseInt(
                configurationService.getOne(ConfigurationKey.NUMBER_OF_GROUP_MEMBERS_ADVANCING).value)

        return (Math.log((groupsCount * numberOfGroupMembersAdvancing).toDouble()) / Math.log(2.0)).toInt()
    }

    /**
     * Playoff tree building with players in first round, number of rounds to final, whether or not there's a three-way
     * final.
     * ```
     *                  +2   +2   +4   +4   +8   +8  +16  +16  +32  +32
     *     players  4    6    8   12   16   24   32   48   64   96  128
     *      rounds  2    2    3    3    4    4    5    5    6    6    7
     *        3way  n    y    n    y    n    y    n    y    n    y    n
     * log2(plyrs)  2   2.6   3   3.6   4   4.6   5   5.6   6   6.6   7
     * #round 3rd        3    4    4    5    5
     * ```
     *
     * Three-way final game pairing.
     * Numbers represent the `spot` they're coming from.
     * ```
     * 1st 2nd 3rd
     * 1n  12  12
     * n1  n1  31
     * nn  2n  23
     * ```
     *
     * In case the given game concludes the tournament, it will be finished.
     *
     * @return Games created or updated by the method. The game(s) that the user has advanced to or none in case it
     * was a final game and there's no further game to advance to. Can be three games when the first user or two games
     * when the other two users reach a three-way final.
     */
    @Transactional
    fun advanceByGame(game: Game): List<Game> {
        val winner = game.winner()
        val loser = game.loser()

        val numberOfRoundsInTournament = getNumberOfPlayoffRoundsInTournament(game.tournament) + 1

        @Suppress("CascadeIf")
        if (isFinalGame(game.tournament, game.playoff!!.round)) {
            val thirdPlaceGame = gameRepository.findByTournamentAndRoundAndNotVoided(game.tournament, game.playoff!!.round - 1)
            if (thirdPlaceGame.size > 1) throw RuntimeException("There's more than one third place game.")
            if (thirdPlaceGame.size < 1) throw RuntimeException("There's no third place game although there's already a final game.")
            if (thirdPlaceGame[0].wasPlayed()) tournamentService.finish(winner, loser, thirdPlaceGame[0].winner(), game.playoff!!.round - 1, false)
            return emptyList()
        } else if (isThirdPlaceGame(game.tournament, game.playoff!!.round)) {
            val finalGame = gameRepository.findByTournamentAndRoundAndNotVoided(game.tournament, game.playoff!!.round + 1)
            if (finalGame.size > 1) throw RuntimeException("There's more than one one-way final game.")
            if (finalGame.size < 1) throw RuntimeException("There's no one-way final game although there's already a third place game.")
            if (finalGame[0].wasPlayed()) tournamentService.finish(finalGame[0].winner(), finalGame[0].loser(), winner, game.playoff!!.round, false)
            return emptyList()
        } else if (isThreeWayFinalGame(game.tournament, game.playoff!!.round)) {
            val threeWayFinalGames = gameRepository.findByTournamentAndRoundAndNotVoided(game.tournament, game.playoff!!.round)
                    .map { if (it == game) game else it }
            if (threeWayFinalGames.size == 3 && !threeWayFinalGames.any { !it.wasPlayed() }) {
                try {
                    val (gold, silver, bronze) = ThreeWayFinalResult.fromThreeWayFinalGames(threeWayFinalGames)
                    tournamentService.finish(gold, silver, bronze, game.playoff!!.round, true)
                } catch (e: TiedThreeWayFinalResult) {
                    return listOf(
                            *gameRepository.saveAll(threeWayFinalGames.onEach { it.voided = true }).toTypedArray(),
                            *gameRepository.saveAll(threeWayFinalGames.map {
                                Game(
                                        homeUser = it.homeUser,
                                        awayUser = it.awayUser,
                                        playoff = PlayoffGame(round = it.playoff!!.round, spot = it.playoff!!.spot),
                                        tournament = it.tournament
                                )
                            }).toTypedArray())
                }
            }
            return emptyList()
        }

        val (nextRound, nextSpot: Int) = determineNextPlayoffSpot(game.playoff!!.round, game.playoff!!.spot)

        val nextRoundIsThreeWayFinal = isThreeWayFinalGame(game.tournament, nextRound)
        val nextGameAsHomeUser = when (game.playoff!!.spot % 2 != 0) {
            true -> true
            false -> nextRoundIsThreeWayFinal
        }

        val affectedGames = mutableListOf<Game>()

        if (nextRoundIsThreeWayFinal) {
            val existingThreeWayFinalGames = gameRepository.findByTournamentAndRoundAndNotVoided(game.tournament, nextRound)

            if (existingThreeWayFinalGames.isEmpty()) {
                affectedGames.add(gameRepository.save(Game(homeUser = winner, playoff = PlayoffGame(round = nextRound, spot = 1), tournament = game.tournament)))
                affectedGames.add(gameRepository.save(Game(awayUser = winner, playoff = PlayoffGame(round = nextRound, spot = 2), tournament = game.tournament)))
                affectedGames.add(gameRepository.save(Game(playoff = PlayoffGame(round = nextRound, spot = 3), tournament = game.tournament)))
            } else {
                existingThreeWayFinalGames
                        .fold(mutableListOf<Game>()) { acc, threeWayFinalGame ->
                            if (threeWayFinalGame.pairingCompleted()
                                    || (threeWayFinalGame.pairingEmpty() && acc.any { it.pairingEmpty() })
                                    || acc.any { it.pairingInvolves(threeWayFinalGame.homeUser) || it.pairingInvolves(threeWayFinalGame.awayUser) }) {
                                return@fold acc
                            }

                            acc.add(threeWayFinalGame)
                            acc
                        }
                        .forEach {
                            if (it.pairingHalfCompleted()) it.pairUser(winner)
                            else it.homeUser = winner
                            affectedGames.add(gameRepository.save(it))
                        }
            }
        } else {
            val gameToAdvanceTo = gameRepository.findGameInPlayoffTree(game.tournament, nextRound, nextSpot)

            if (gameToAdvanceTo.isPresent) {
                when {
                    gameToAdvanceTo.get().homeUser == null -> gameToAdvanceTo.get().homeUser = winner
                    gameToAdvanceTo.get().awayUser == null -> gameToAdvanceTo.get().awayUser = winner
                    else -> throw RuntimeException("gameToAdvanceTo already has a home and away user.")
                }

                affectedGames.add(gameRepository.save(gameToAdvanceTo.get()))
            } else {
                affectedGames.add(gameRepository.save(Game(
                        homeUser = if (nextGameAsHomeUser) winner else null,
                        awayUser = if (nextGameAsHomeUser) null else winner,
                        playoff = PlayoffGame(round = nextRound, spot = nextSpot),
                        tournament = game.tournament
                )))
            }
        }

        return affectedGames
    }

    private fun determineNextPlayoffSpot(currentRound: Int, currentSpot: Int): Pair<Int, Int> {
        val nextRound = currentRound + 1
        val nextSpot: Int
        if (currentSpot % 2 != 0) {
            nextSpot = (currentSpot + 1) / 2
        } else {
            nextSpot = currentSpot / 2
        }
        return Pair(nextRound, nextSpot)
    }

    @Transactional
    @Throws(PlayoffGameNotVoidableException::class, IllegalStateException::class)
    fun voidPlayoffGame(game: Game): Game {
        if (!getVoidableGames().contains(game)) throw PlayoffGameNotVoidableException("Game ${game.id} must not be voided.")

        game.voided = true

        if (!isSomeKindOfFinalGame(game)) {
            val (nextRound, nextSpot) = determineNextPlayoffSpot(game.playoff!!.round, game.playoff!!.spot)
            val gameToAdvanceTo = gameRepository.findGameInPlayoffTree(game.tournament, nextRound, nextSpot)

            if (gameToAdvanceTo.isPresent) {
                if (gameToAdvanceTo.get().wasPlayed()) {
                    throw IllegalStateException("Cannot void playoff game whose following game has already been played.")
                }

                gameRepository.delete(gameToAdvanceTo.get())

                if (isThirdPlaceGame(gameToAdvanceTo.get().tournament, gameToAdvanceTo.get().playoff!!.round)) {
                    val finalGame = gameRepository.findGameInPlayoffTree(
                            gameToAdvanceTo.get().tournament,
                            gameToAdvanceTo.get().playoff!!.round + 1, 1) // final

                    if (finalGame.get().wasPlayed()) {
                        throw IllegalStateException("Cannot void playoff semifinal game whose following final game has already been played.")
                    }

                    gameRepository.delete(finalGame.get())
                }
            } else {
                this.logger.warn("Playoff game ${game.id} has been played but a subsequent game has not been found in the playoff tree.")
            }
        }

        return gameRepository.save(Game(
                homeUser = game.homeUser,
                awayUser = game.awayUser,
                tournament = game.tournament,
                playoff = game.playoff!!.copy(id = null)
        ))
    }

    inner class PlayoffGameNotVoidableException internal constructor(message: String) : RuntimeException(message)
}
