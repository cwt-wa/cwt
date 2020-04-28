package com.cwtsite.cwt.domain.application.service

import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.entity.User
import com.cwtsite.cwt.entity.Application
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ApplicationService @Autowired
constructor(private val applicationRepository: ApplicationRepository, private val tournamentService: TournamentService) {

    @Throws(AlreadyAppliedException::class)
    fun apply(user: User): Application {
        val currentTournament = tournamentService.getCurrentTournament()!!
        if (applicationRepository.findByApplicantAndTournament(user, currentTournament).isPresent) {
            throw AlreadyAppliedException("User ${user.id} has already applied to tournament ${currentTournament.id}")
        }
        return applicationRepository.save(Application(currentTournament, user))
    }

    inner class AlreadyAppliedException(message: String) : RuntimeException(message)
}
