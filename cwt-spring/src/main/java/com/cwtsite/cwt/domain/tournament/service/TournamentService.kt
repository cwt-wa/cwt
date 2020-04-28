package com.cwtsite.cwt.domain.tournament.service

import com.cwtsite.cwt.domain.application.service.ApplicationRepository
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.playoffs.service.TreeService
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.entity.Application
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class TournamentService {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var tournamentRepository: TournamentRepository

    @Autowired
    private lateinit var applicationRepository: ApplicationRepository

    @Autowired
    private lateinit var gameRepository: GameRepository

    @Autowired
    private lateinit var treeService: TreeService


    /**
     * @throws IllegalStateException When there are unfinished tournaments.
     */
    @Throws(IllegalStateException::class)
    fun startNewTournament(moderatorIds: List<Long>): Tournament {
        if (tournamentRepository.countByStatusNot(TournamentStatus.FINISHED) > 0) {
            throw IllegalStateException("For a new tournament to start it is required that all tournaments are finished.")
        }

        val tournament = Tournament()
        tournament.moderators = userRepository.findAllById(moderatorIds).toMutableSet()
        tournament.status = TournamentStatus.OPEN

        return tournamentRepository.save(tournament)
    }

    @Transactional
    fun startGroups(): Tournament {
        val tournament = getCurrentTournament()!!
        tournament.status = TournamentStatus.GROUP
        return tournamentRepository.save(tournament)
    }

    @Transactional
    fun startPlayoffs(games: List<Game>): List<Game> {
        val currentTournament = getCurrentTournament()!!
        currentTournament.status = TournamentStatus.PLAYOFFS
        currentTournament.maxRounds = treeService.getNumberOfPlayoffRoundsInTournament(currentTournament)
        currentTournament.threeWay = treeService.isPlayoffTreeWithThreeWayFinal(currentTournament)
        tournamentRepository.save(currentTournament)
        return gameRepository.saveAll(games)
    }

    @Transactional
    fun finish(gold: User, silver: User, bronze: User, maxRounds: Int, numOfGroupAdvancing: Int, threeWay: Boolean): Tournament {
        val currentTournament = getCurrentTournament()!!
        currentTournament.goldWinner = gold
        currentTournament.silverWinner = silver
        currentTournament.bronzeWinner = bronze
        currentTournament.status = TournamentStatus.FINISHED
        currentTournament.maxRounds = maxRounds
        currentTournament.numOfGroupAdvancing = numOfGroupAdvancing
        currentTournament.threeWay = threeWay
        val savedTournament = tournamentRepository.save(currentTournament)
        userRepository.refreshUserStats()
        return savedTournament
    }

    fun getTournament(id: Long): Optional<Tournament> {
        return tournamentRepository.findById(id)
    }

    fun getApplicants(tournament: Tournament): List<Application> {
        return this.applicationRepository.findByTournament(tournament)
    }

    fun getTournamentByYear(year: Long): Optional<Tournament> {
        return tournamentRepository.findByYear(year.toInt())
    }

    @Throws(RuntimeException::class)
    fun getCurrentTournament(): Tournament? =
            tournamentRepository.findByStatusNot(TournamentStatus.FINISHED)

    fun getAll(): List<Tournament> = tournamentRepository.findAll()

    fun getAllFinished(): List<Tournament> = tournamentRepository.findByStatus(TournamentStatus.FINISHED)
}
