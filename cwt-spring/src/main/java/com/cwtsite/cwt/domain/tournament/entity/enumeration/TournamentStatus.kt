package com.cwtsite.cwt.domain.tournament.entity.enumeration

enum class TournamentStatus {

    /**
     * Open for applications.
     */
    OPEN,

    /**
     * Applicants have been drawn into groups
     * and participants can now report their group stage games.
     */
    GROUP,

    /**
     * The playoffs pairings have been drawn and can now be reported.
     */
    PLAYOFFS,

    /**
     * All games have been played; gold, silver and bronze have been determined.
     * Processes to round up the tournament have been spawned (timeline calculations, all-time ranking etc.)
     */
    FINISHED,

    /**
     * Tournament has been put into the archive.
     *
     * There must not be more than one tournament *not* in this state. Because the one tournament that is not in this
     * state is what is used for all the business processes.
     */
    ARCHIVED
}
