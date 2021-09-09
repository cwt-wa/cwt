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
import java.time.Duration
import java.time.Instant
import java.util.Optional

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
        // group stage plus number of playoff rounds
        currentTournament.maxRounds = treeService.getNumberOfPlayoffRoundsInTournament(currentTournament) + 1
        currentTournament.threeWay = treeService.isPlayoffTreeWithThreeWayFinal(currentTournament)
        tournamentRepository.save(currentTournament)
        return gameRepository.saveAll(games)
    }

    @Transactional
    fun finish(gold: User, silver: User, bronze: User): Tournament {
        val currentTournament = getCurrentTournament()!!
        currentTournament.goldWinner = gold
        currentTournament.silverWinner = silver
        currentTournament.bronzeWinner = bronze
        currentTournament.status = TournamentStatus.FINISHED
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

    /**
     * Get the tournament whose creation is after and closest to the given creation.
     *
     * @return Can be `null` when there's no tournament created before the given creation instant.
     */
    fun getCurrentTournamentAt(at: Instant): Tournament? =
            tournamentRepository.findByCreatedBefore(at)
              .minByOrNull { Duration.between(it.created, at) }

    fun getAll(): List<Tournament> = tournamentRepository.findAll()

    fun getAllFinished(): List<Tournament> = tournamentRepository.findByStatus(TournamentStatus.FINISHED)

    fun save(tournament: Tournament): Tournament = tournamentRepository.save(tournament)
}
