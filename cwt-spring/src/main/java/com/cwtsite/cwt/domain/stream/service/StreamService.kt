package com.cwtsite.cwt.domain.stream.service

import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.group.service.GroupRepository
import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.stream.entity.Stream
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.util.*
import kotlin.math.abs

@Service
class StreamService {


    @Autowired
    private lateinit var streamRepository: StreamRepository

    @Autowired
    private lateinit var channelRepository: ChannelRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var groupRepository: GroupRepository

    @Autowired
    private lateinit var gameRepository: GameRepository

    @Autowired
    private lateinit var tournamentService: TournamentService

    private val matchThreshold = 75
    private val streamGameTolerance = Duration.ofHours(5)

    // todo call this when reporting and when stream is found
    //  whatever finds the match first
    // todo then publish news maybe?
    // todo update stream overview and detail page with associated game
    // todo update game detail page to show the associated game
    // todo onboarding on user detail page
    fun findMatchingGame(stream: Stream): Game? {
        val tournament = tournamentService.getCurrentTournament()
        // todo maybe only include usernames of games which have not yet been associated to a game
        val usernames = if (tournament == null) {
            userRepository.findAllUsernamesToLowerCase().toSet()
        } else {
            when (tournament.status) {
                TournamentStatus.GROUP -> groupRepository.findDistinctUsernamesToLowercase(tournament)
                TournamentStatus.PLAYOFFS -> setOf(
                        *gameRepository.findDistinctHomeUsernamesToLowercaseInPlayoffs(tournament).toTypedArray(),
                        *gameRepository.findDistinctAwayUsernamesToLowercaseInPlayoffs(tournament).toTypedArray())
                else -> userRepository.findAllUsernamesToLowerCase().toSet()
            }
        }
        val res = stream.relevantWordsInTitle(usernames)
                .asSequence()
                .map { word ->
                    usernames
                            .map { username -> Pair(username, FuzzySearch.ratio(username, word)) }
                            .maxBy { it.second }!!
                }
                .filter { it.second >= matchThreshold }
                .sortedByDescending { it.second }
                .take(2)
                .map { it.first }
                .toList()
        if (res.size != 2) return null
        val user1 = userRepository.findByUsernameIgnoreCase(res[0])
        val user2 = userRepository.findByUsernameIgnoreCase(res[1])
        return when (tournament) {
            null -> gameRepository.findGame(user1!!, user2!!)
            else -> gameRepository.findGame(user1!!, user2!!, tournament)
        }
                .filter {
                    when (tournament?.status) {
                        TournamentStatus.GROUP -> it.playoff == null
                        TournamentStatus.PLAYOFFS -> it.playoff != null
                        else -> true
                    }
                }
                .maxBy { it.created!! }
    }

    @Transactional
    fun saveStreams(streams: Collection<Stream>): List<Stream> {
        streams
                .filter { it.game != null }
                .onEach { findMatchingGame(it)?.let { game -> it.game = game } }
        return streamRepository.saveAll(streams)
    }

    fun findMatchingStreams(game: Game): Set<Stream> {
        val streams = streamRepository.findWithoutGame()
                .filter {
                    (abs(it.createdAtAsTimestamp().time - game.reportedAt!!.time)
                        < streamGameTolerance.toMillis())
                }
        if (streams.isEmpty()) return emptySet()
        val usernames = userRepository.findAllUsernamesToLowerCase().toSet()
        return streams
                .map { stream ->
                    val relevantWordsInTitle = stream.relevantWordsInTitle(usernames)
                    val bestHomeUserMatch = relevantWordsInTitle
                            .map { word -> FuzzySearch.ratio(word, game.homeUser!!.username.toLowerCase()) }
                            .max()!!
                    val bestAwayUserMatch = relevantWordsInTitle
                            .map { word -> FuzzySearch.ratio(word, game.awayUser!!.username.toLowerCase()) }
                            .max()!!

                    Triple(stream, bestHomeUserMatch, bestAwayUserMatch)
                }
                .filter { it.second >= matchThreshold && it.third >= matchThreshold }
                .map { it.first }
                .toSet()
    }

    fun findAll(): List<Stream> = streamRepository.findAll()

    fun findChannel(channelId: String): Optional<Channel> = channelRepository.findById(channelId)

    fun findAllChannels(): List<Channel> = channelRepository.findAll()

    fun findOne(id: String): Optional<Stream> = streamRepository.findById(id)

    fun saveChannel(channel: Channel): Channel = channelRepository.save(channel)

    fun findChannelByUsers(users: List<User>): List<Channel> = channelRepository.findAllByUserIn(users)

    fun saveVideoCursor(channel: Channel, videoCursor: String?): Channel =
            channelRepository.save(with (channel) { this.videoCursor = videoCursor; this})
}
