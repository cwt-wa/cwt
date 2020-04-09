package com.cwtsite.cwt.domain.playoffs.service

import com.cwtsite.cwt.domain.configuration.entity.Configuration
import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.configuration.service.ConfigurationRepository
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.PlayoffGame
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.playoffs.ThreeWayFinalResult
import com.cwtsite.cwt.domain.playoffs.TiedThreeWayFinalResult
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.NoSuchElementException

@Component
class PlayoffService {

    @Autowired
    private lateinit var gameRepository: GameRepository

    @Autowired
    private lateinit var tournamentService: TournamentService

    @Autowired
    private lateinit var treeService: TreeService

    @Autowired
    private lateinit var configurationRepository: ConfigurationRepository

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getGamesOfTournament(tournament: Tournament, includeVoided: Boolean = true): List<Game> {
        return if (includeVoided) gameRepository.findByTournamentAndPlayoffIsNotNull(tournament)
        else gameRepository.findByTournamentAndPlayoffIsNotNullAndVoidedFalse(tournament)
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

        val numOfGroupAdvancing = configurationRepository
                .findById(ConfigurationKey.NUMBER_OF_GROUP_MEMBERS_ADVANCING)
                .orElseThrow { IllegalStateException() }

        @Suppress("CascadeIf")
        if (treeService.isFinalGame(game.tournament, game.playoff!!.round)) {
            val thirdPlaceGame = gameRepository.findByTournamentAndRoundAndNotVoided(game.tournament, game.playoff!!.round - 1)
            if (thirdPlaceGame.size > 1) throw RuntimeException("There's more than one third place game.")
            if (thirdPlaceGame.size < 1) throw RuntimeException("There's no third place game although there's already a final game.")
            if (thirdPlaceGame[0].wasPlayed()) {
                tournamentService.finish(
                        winner, loser, thirdPlaceGame[0].winner(),
                        game.playoff!!.round - 1, numOfGroupAdvancing.value!!.toInt(), false)
            }
            return emptyList()
        } else if (treeService.isThirdPlaceGame(game.tournament, game.playoff!!.round)) {
            val finalGame = gameRepository.findByTournamentAndRoundAndNotVoided(game.tournament, game.playoff!!.round + 1)
            if (finalGame.size > 1) throw RuntimeException("There's more than one one-way final game.")
            if (finalGame.size < 1) throw RuntimeException("There's no one-way final game although there's already a third place game.")
            if (finalGame[0].wasPlayed()) {
                tournamentService.finish(
                        finalGame[0].winner(), finalGame[0].loser(), winner,
                        game.playoff!!.round, numOfGroupAdvancing.value!!.toInt(), false)
            }
            return emptyList()
        } else if (treeService.isThreeWayFinalGame(game.tournament, game.playoff!!.round)) {
            val threeWayFinalGames = gameRepository.findByTournamentAndRoundAndNotVoided(game.tournament, game.playoff!!.round)
                    .map { if (it == game) game else it }
            if (threeWayFinalGames.size == 3 && !threeWayFinalGames.any { !it.wasPlayed() }) {
                try {
                    val (gold, silver, bronze) = ThreeWayFinalResult.fromThreeWayFinalGames(threeWayFinalGames)
                    tournamentService.finish(gold, silver, bronze, game.playoff!!.round, numOfGroupAdvancing.value!!.toInt(), true)
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

        val (nextRound, nextSpot: Int) = treeService.nextPlayoffSpotForOneWayFinalTree(game.playoff!!.round, game.playoff!!.spot)
        val nextRoundIsThreeWayFinal = treeService.isThreeWayFinalGame(game.tournament, nextRound)
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
            if (treeService.isSemifinalGame(game.tournament, game.playoff!!.round)) {
                affectedGames.add(
                        advanceBasedOnGameExistence(
                                loser, nextRound, 1, game.playoff!!.spot, game.tournament)) // little final

                affectedGames.add(
                        advanceBasedOnGameExistence(
                                winner, nextRound + 1, 1, game.playoff!!.spot, game.tournament)) // final
            } else {
                affectedGames.add(
                        advanceBasedOnGameExistence(
                                winner, nextRound, nextSpot, game.playoff!!.spot, game.tournament))
            }
        }

        return affectedGames
    }

    /**
     * When you advance you wouldn't want to create the game to advance to when it's already there
     * because your upcoming opponent has already advanced to it.
     */
    private fun advanceBasedOnGameExistence(userToAdvance: User,
                                            destinationRound: Int, destinationSpot: Int,
                                            originSpot: Int, tournament: Tournament): Game {
        val nextGameAsHomeUser = originSpot % 2 != 0
        val gameToAdvanceTo = gameRepository.findGameInPlayoffTree(tournament, destinationRound, destinationSpot)

        return if (gameToAdvanceTo.isPresent) {
            when {
                gameToAdvanceTo.get().homeUser == null -> gameToAdvanceTo.get().homeUser = userToAdvance
                gameToAdvanceTo.get().awayUser == null -> gameToAdvanceTo.get().awayUser = userToAdvance
                else -> throw RuntimeException("gameToAdvanceTo already has a home and away user.")
            }
            gameRepository.save(gameToAdvanceTo.get())
        } else {
            gameRepository.save(Game(
                    homeUser = if (nextGameAsHomeUser) userToAdvance else null,
                    awayUser = if (nextGameAsHomeUser) null else userToAdvance,
                    playoff = PlayoffGame(round = destinationRound, spot = destinationSpot),
                    tournament = tournament
            ))
        }
    }

    @Transactional
    @Throws(PlayoffGameNotVoidableException::class, IllegalStateException::class)
    fun voidPlayoffGame(game: Game): Game {
        if (!treeService.getVoidablePlayoffGames().contains(game)) throw PlayoffGameNotVoidableException("Game ${game.id} must not be voided.")

        game.voided = true

        if (!treeService.isSomeKindOfFinalGame(game)) {
            val (nextRound, nextSpot) = treeService.nextPlayoffSpotForOneWayFinalTree(game.playoff!!.round, game.playoff!!.spot)

            if (treeService.isThreeWayFinalGame(game.tournament, nextRound)) {
                val threeWayGamesToAdvanceTo = gameRepository.findGameInPlayoffTree(game.tournament, game.winner(), nextRound)
                threeWayGamesToAdvanceTo
                        .filter { !it.wasPlayed() }
                        .filter { it.pairingInvolves(game.winner()) }
                        .forEach { treeService.removePartOfPlayoffGame(it, game.winner(), true) }
            } else {
                val gameToAdvanceTo = gameRepository.findGameInPlayoffTree(game.tournament, nextRound, nextSpot)

                if (gameToAdvanceTo.isPresent) {
                    treeService.removePartOfPlayoffGame(gameToAdvanceTo.get(), game.winner(), false)

                    if (treeService.isThirdPlaceGame(gameToAdvanceTo.get().tournament, gameToAdvanceTo.get().playoff!!.round)) {
                        val finalGame = gameRepository.findGameInPlayoffTree(
                                gameToAdvanceTo.get().tournament,
                                gameToAdvanceTo.get().playoff!!.round + 1, 1) // final

                        treeService.removePartOfPlayoffGame(finalGame.get(), game.loser(), false)
                    }
                } else {
                    this.logger.warn("Playoff game ${game.id} has been played but a subsequent game has not been found in the playoff tree.")
                }
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
