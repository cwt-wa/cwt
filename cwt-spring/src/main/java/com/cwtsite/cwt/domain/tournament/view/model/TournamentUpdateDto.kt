package com.cwtsite.cwt.domain.tournament.view.model

import com.cwtsite.cwt.domain.core.DataTransferObject
import com.cwtsite.cwt.domain.tournament.entity.Tournament
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.user.repository.entity.User

@DataTransferObject
data class TournamentUpdateDto(
        val status: TournamentStatus?,
        val review: String?,
        val maxRounds: Int?,
        val numOfGroupAdvancing: Int?,
        val bronzeWinner: Long?,
        val silverWinner: Long?,
        val goldWinner: Long?,
        val moderators: List<Long>?
) {

    fun update(tournament: Tournament, findUserById: (userId: Long?) -> User?): Tournament {
        tournament.status = status ?: tournament.status;
        tournament.review = review ?: tournament.review;
        tournament.maxRounds = maxRounds ?: tournament.maxRounds;
        tournament.numOfGroupAdvancing = numOfGroupAdvancing ?: tournament.numOfGroupAdvancing;
        tournament.bronzeWinner = findUserById(bronzeWinner) ?: tournament.bronzeWinner;
        tournament.silverWinner = findUserById(silverWinner) ?: tournament.silverWinner;
        tournament.goldWinner = findUserById(goldWinner) ?: tournament.goldWinner;
        tournament.moderators = moderators?.map { findUserById(it)!! }?.toMutableSet() ?: tournament.moderators;
        return tournament
    }
}
