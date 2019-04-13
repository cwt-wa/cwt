package com.cwtsite.cwt.domain.playoffs.service

import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.configuration.service.ConfigurationService
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.entity.PlayoffGame
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.group.service.GroupRepository
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class PlayoffService @Autowired
constructor(private val gameRepository: GameRepository, private val tournamentService: TournamentService,
            private val groupRepository: GroupRepository, private val configurationService: ConfigurationService) {

    fun getGamesOfTournament(tournament: Tournament): List<Game> {
        return gameRepository.findByTournamentAndPlayoffIsNotNull(tournament)
    }

    fun getNextGameForUser(user: User): Game {
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

    private fun isFinalGame(game: Game): Boolean {
        return game.playoff!!.round == getNumberOfPlayoffRoundsInTournament(game.tournament) + 1
    }

    private fun isThirdPlaceGame(game: Game): Boolean {
        return game.playoff!!.round == getNumberOfPlayoffRoundsInTournament(game.tournament)
    }

    private fun getNumberOfPlayoffRoundsInTournament(tournament: Tournament): Int {
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
     *      rounds  2    2    3    3    4    4    5    5    6    6    8
     *        3way  n    y    n    y    n    y    n    y    n    y    n
     * log2(plyrs)  2   2.6   3   3.6   4   4.6   5   5.6   6   6.6   7
     * ```
     *
     * Three-way final game pairing.
     * Numbers represent the `spot` they're coming from.
     * ```
     *
     * 1st 2nd 3rd
     * 1n  12  12
     * n1  n1  31
     * nn  2n  23
     *
     * ```
     * @return Games created or updated by the method. The game(s) that the user has advanced to or none in case it
     * was a final game and there's no further game to advance to. Can be three games when the first user or two games
     * when the other two users reach a three-way final.
     */
    @Transactional
    fun advanceByGame(game: Game): List<Game> {
        if (isFinalGame(game) || isThirdPlaceGame(game) || roundIsThreeWayFinal(game.tournament, game.playoff!!.round)) {
            return emptyList()
        }

        val winner = (if (game.scoreHome!! > game.scoreAway!!) game.homeUser else game.awayUser)!!

        val nextRound = game.playoff!!.round + 1
        val nextSpot: Int
        val nextGameAsHomeUser: Boolean
        val nextRoundIsThreeWayFinal = roundIsThreeWayFinal(game.tournament, nextRound)
        if (game.playoff!!.spot % 2 != 0) {
            nextSpot = (game.playoff!!.spot + 1) / 2
            nextGameAsHomeUser = true
        } else {
            nextSpot = game.playoff!!.spot / 2
            nextGameAsHomeUser = nextRoundIsThreeWayFinal
        }

        val affectedGames = mutableListOf<Game>()

        if (nextRoundIsThreeWayFinal) {
            val existingThreeWayFinalGames = gameRepository.findByTournamentAndRound(game.tournament, nextRound)

            if (existingThreeWayFinalGames.isEmpty()) {
                affectedGames.add(gameRepository.save(Game(homeUser = winner, playoff = PlayoffGame(round = nextRound, spot = 1), tournament = game.tournament)))
                affectedGames.add(gameRepository.save(Game(awayUser = winner, playoff = PlayoffGame(round = nextRound, spot = 2), tournament = game.tournament)))
                affectedGames.add(gameRepository.save(Game(playoff = PlayoffGame(round = nextRound, spot = 3), tournament = game.tournament)))
            } else {
                existingThreeWayFinalGames
                        .fold(mutableListOf<Game>()) { acc, threeWayFinalGame ->
                            if (threeWayFinalGame.completedPairing()
                                    || (threeWayFinalGame.emptyPairing() && acc.any { it.emptyPairing() })
                                    || acc.any { it.wasPlayedBy(threeWayFinalGame.homeUser) || it.wasPlayedBy(threeWayFinalGame.awayUser) }) {
                                return@fold acc
                            }

                            acc.add(threeWayFinalGame)
                            acc
                        }
                        .forEach {
                            if (it.isHalfPaired()) it.pairUser(winner)
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

    private fun roundIsThreeWayFinal(tournament: Tournament, round: Int): Boolean {
        val playersInFirstRound = gameRepository.findByTournamentAndRound(tournament, 1).size * 2
        val isPlayoffTreeWithThreeWayFinal = kotlin.math.log2(playersInFirstRound.toDouble()) % 1 != 0.0
        if (!isPlayoffTreeWithThreeWayFinal) return false
        val numOfRounds = (Math.log((playersInFirstRound).toDouble()) / Math.log(2.0))
        return Math.floor(numOfRounds) == round.toDouble()
    }
}
