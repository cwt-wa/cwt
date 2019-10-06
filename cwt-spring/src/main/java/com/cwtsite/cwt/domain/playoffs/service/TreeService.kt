package com.cwtsite.cwt.domain.playoffs.service

import com.cwtsite.cwt.domain.configuration.entity.enumeratuion.ConfigurationKey
import com.cwtsite.cwt.domain.configuration.service.ConfigurationService
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.group.service.GroupRepository
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.log2

@Component
class TreeService {

    @Autowired lateinit var tournamentService: TournamentService
    @Autowired lateinit var gameRepository: GameRepository
    @Autowired lateinit var configurationService: ConfigurationService
    @Autowired lateinit var groupRepository: GroupRepository

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getVoidablePlayoffGames(): List<Game> {
        return gameRepository.findByTournamentAndPlayoffIsNotNull(tournamentService.getCurrentTournament())
                .filter {
                    if (!it.wasPlayed()) return@filter false
                    if (isSomeKindOfFinalGame(it)) return@filter true
                    val (nextRound, nextSpot) = nextPlayoffSpotForOneWayFinalTree(it.playoff!!.round, it.playoff!!.spot)
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

    fun isSomeKindOfFinalGame(game: Game): Boolean =
            isFinalGame(game.tournament, game.playoff!!.round)
                    || isThirdPlaceGame(game.tournament, game.playoff!!.round)
                    || isThreeWayFinalGame(game.tournament, game.playoff!!.round)

    fun isFinalGame(tournament: Tournament, round: Int): Boolean {
        return round == getNumberOfPlayoffRoundsInTournament(tournament) + 1 && !isThreeWayFinalGame(tournament, round)
    }

    fun isThirdPlaceGame(tournament: Tournament, round: Int): Boolean {
        return round == getNumberOfPlayoffRoundsInTournament(tournament) && !isThreeWayFinalGame(tournament, round)
    }

    fun isThreeWayFinalGame(tournament: Tournament, round: Int): Boolean {
        val playersInFirstRound = gameRepository.findByTournamentAndRoundAndNotVoided(tournament, 1).size * 2
        val isPlayoffTreeWithThreeWayFinal = isPlayoffTreeWithThreeWayFinal(playersInFirstRound)
        if (!isPlayoffTreeWithThreeWayFinal) return false
        val numOfRounds = (ln((playersInFirstRound).toDouble()) / ln(2.0))
        return floor(numOfRounds) == round.toDouble()
    }

    fun nextPlayoffSpotForOneWayFinalTree(currentRound: Int, currentSpot: Int): Pair<Int, Int> {
        val nextRound = currentRound + 1
        val nextSpot = if (currentSpot % 2 != 0) {
            (currentSpot + 1) / 2
        } else {
            currentSpot / 2
        }
        return Pair(nextRound, nextSpot)
    }

    fun getNumberOfPlayoffRoundsInTournament(tournament: Tournament): Int {
        val groupsCount = groupRepository.countByTournament(tournament)
        val numberOfGroupMembersAdvancing = Integer.parseInt(
                configurationService.getOne(ConfigurationKey.NUMBER_OF_GROUP_MEMBERS_ADVANCING).value)

        return (ln((groupsCount * numberOfGroupMembersAdvancing).toDouble()) / ln(2.0)).toInt()
    }

    fun onlyFinalGamesAreLeftToPlay(): Boolean {
        val currentTournament = tournamentService.getCurrentTournament()
        return if (currentTournament.status == TournamentStatus.PLAYOFFS) {
            gameRepository.findReadyGamesInRoundEqualOrGreaterThan(
                    getNumberOfPlayoffRoundsInTournament(currentTournament)).size == 2
        } else {
            false
        }
    }

    fun isPlayoffTreeWithThreeWayFinal(tournament: Tournament) =
            isPlayoffTreeWithThreeWayFinal(gameRepository.findByTournamentAndRoundAndNotVoided(tournament, 1).size * 2)

    fun getNextGameForUser(user: User): Game? =
            gameRepository.findNextPlayoffGameForUser(tournamentService.getCurrentTournament(), user)

    private fun isPlayoffTreeWithThreeWayFinal(playersInFirstRound: Int) =
            log2(playersInFirstRound.toDouble()) % 1 != 0.0
}
