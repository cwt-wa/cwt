package com.cwtsite.cwt.domain.group.service

import com.cwtsite.cwt.domain.configuration.service.ConfigurationService
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.group.entity.Group
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.service.TournamentRepository
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.entity.GroupStanding
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GroupService @Autowired
constructor(private val groupRepository: GroupRepository,
            private val tournamentRepository: TournamentRepository, private val configurationService: ConfigurationService,
            private val userRepository: UserRepository, private val tournamentService: TournamentService,
            private val gameRepository: GameRepository) {

    @Transactional
    fun replacePlayer(idOfUserObsolete: Long, idOfUserNew: Long): Group {
        val obsoleteUser = userRepository.findById(idOfUserObsolete).orElseThrow { IllegalArgumentException() }
        val replacementUser = userRepository.findById(idOfUserNew).orElseThrow { IllegalArgumentException() }

        val group = groupRepository.findByTournamentAndUser(
                tournamentService.getCurrentTournament(), obsoleteUser) ?: throw RuntimeException()

        (group.standings.find { it.user == obsoleteUser } ?: throw RuntimeException()).user = replacementUser
        group.standings.onEach { it.reset() }

        val gamesOfGroup = gameRepository.findByGroup(group)

        gamesOfGroup
                .filter { it.wasPlayedBy(obsoleteUser) }
                .onEach { it.voided = true }

        gamesOfGroup
                .filterNot { it.voided }
                .onEach { calcTableByGame(it) }

        return group
    }

    fun calcTableByGame(game: Game) {
        val standingOfHomeUser = game.group!!.standings
                .find { it.user == game.homeUser } ?: throw IllegalArgumentException()

        val standingOfAwayUser = game.group!!.standings
                .find { it.user == game.awayUser } ?: throw IllegalArgumentException()

        val pointsPattern = configurationService.parsedPointsPatternConfiguration

        val standingOfWinner: GroupStanding
        val standingOfLoser: GroupStanding

        if (game.scoreHome!! > game.scoreAway!!) {
            standingOfWinner = standingOfHomeUser
            standingOfLoser = standingOfAwayUser

            standingOfWinner.points = standingOfWinner.points + getPointsForScore(pointsPattern, game.scoreHome!!)
            standingOfLoser.points = standingOfLoser.points + getPointsForScore(pointsPattern, game.scoreAway!!)

            standingOfWinner.roundRatio = standingOfWinner.roundRatio + (game.scoreHome!! - game.scoreAway!!)
            standingOfLoser.roundRatio = standingOfLoser.roundRatio + (game.scoreAway!! - game.scoreHome!!)
        } else {
            standingOfWinner = standingOfAwayUser
            standingOfLoser = standingOfHomeUser

            standingOfWinner.points = standingOfWinner.points + getPointsForScore(pointsPattern, game.scoreAway!!)
            standingOfLoser.points = standingOfLoser.points + getPointsForScore(pointsPattern, game.scoreHome!!)

            standingOfWinner.roundRatio = standingOfWinner.roundRatio + (game.scoreAway!! - game.scoreHome!!)
            standingOfLoser.roundRatio = standingOfLoser.roundRatio + (game.scoreHome!! - game.scoreAway!!)
        }

        standingOfWinner.gameRatio = standingOfWinner.gameRatio + 1
        standingOfLoser.gameRatio = standingOfLoser.gameRatio - 1

        standingOfWinner.games = standingOfWinner.games + 1
        standingOfLoser.games = standingOfLoser.games + 1

        groupStandingRepository.saveAll(listOf(standingOfWinner, standingOfLoser))
    }

    private fun getPointsForScore(pointsPattern: List<IntArray>, score: Int): Int =
            pointsPattern.find { it[0] == score }?.get(1) ?: 0

    @Transactional
    fun startGroupStage(tournament: Tournament, groups: List<Group>): List<Group> {
        tournament.status = TournamentStatus.GROUP
        tournamentRepository.save(tournament)
        return groupRepository.saveAll(groups)
    }

    fun save(groups: List<Group>): List<Group> = groupRepository.saveAll(groups)

    fun findAllGroupMembers(tournament: Tournament): List<User> = groupRepository.findAllGroupMembers(tournament)

    fun getGroupsForTournament(tournament: Tournament): List<Group> = groupRepository.findByTournament(tournament)
}
