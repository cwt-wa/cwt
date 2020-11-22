package com.cwtsite.cwt.domain.stream.service

import com.cwtsite.cwt.core.news.PublishNews
import com.cwtsite.cwt.domain.game.entity.Game
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.stream.entity.Stream
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.slf4j.LoggerFactory
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
    private lateinit var gameRepository: GameRepository

    @Autowired
    private lateinit var tournamentService: TournamentService

    private val matchThreshold = 75
    private val streamGameTolerance = Duration.ofHours(5)
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun findMatchingGame(stream: Stream): Game? {
        val tournament = tournamentService.getCurrentTournament()
        logger.info("Finding matching game for stream \"${stream.title}\" in tournament ${tournament?.id}")
        val usernames = if (tournament == null) {
            setOf(
                    *streamRepository.findDistinctHomeUsernamesToLowercase().toTypedArray(),
                    *streamRepository.findDistinctAwayUsernamesToLowercase().toTypedArray())
        } else {
            when (tournament.status) {
                TournamentStatus.GROUP -> setOf(
                        *streamRepository.findDistinctHomeUsernamesToLowercaseInGroup(tournament).toTypedArray(),
                        *streamRepository.findDistinctAwayUsernamesToLowercaseInGroup(tournament).toTypedArray())
                TournamentStatus.PLAYOFFS -> setOf(
                        *streamRepository.findDistinctHomeUsernamesToLowercaseInPlayoffs(tournament).toTypedArray(),
                        *streamRepository.findDistinctAwayUsernamesToLowercaseInPlayoffs(tournament).toTypedArray())
                else -> setOf(
                        *streamRepository.findDistinctHomeUsernamesToLowercase().toTypedArray(),
                        *streamRepository.findDistinctAwayUsernamesToLowercase().toTypedArray())
            }
        }
        logger.info("usernames available for fuzzy matching: $usernames")
        val matchingUsernames = stream.relevantWordsInTitle(usernames)
                .asSequence()
                .map { word ->
                    usernames
                            .map { username -> Pair(username, FuzzySearch.ratio(username, word)) }
                            .maxBy { it.second }
                }
                .filterNotNull()
                .filter { it.second >= matchThreshold }
                .sortedByDescending { it.second }
                .take(2)
                .map { it.first }
                .toList()
        logger.info("Matching usernames: $matchingUsernames")
        if (matchingUsernames.size != 2) return null
        val user1 = userRepository.findByUsernameIgnoreCase(matchingUsernames[0])
        val user2 = userRepository.findByUsernameIgnoreCase(matchingUsernames[1])
        if (user1 == null || user2 == null) return null
        logger.info("Usernames were found in the database, finding the games with them")
        return when (tournament) {
            null -> gameRepository.findGame(user1, user2)
            else -> gameRepository.findGame(user1, user2, tournament)
        }
                .filter {
                    when (tournament?.status) {
                        TournamentStatus.GROUP -> it.playoff == null
                        TournamentStatus.PLAYOFFS -> it.playoff != null
                        else -> true
                    }
                }
                .maxBy { it.reportedAt!! }
    }

    @Transactional
    @PublishNews
    fun associateGame(stream: Stream, game: Game): Stream {
        logger.info("Link game $game to stream $stream")
        stream.game = game
        return stream
    }

    fun findMatchingStreams(game: Game): Set<Stream> {
        val streams = streamRepository.findByGameIsNull()
                .filter {
                    (abs(it.createdAtAsTimestamp().time - game.reportedAt!!.time)
                            < streamGameTolerance.toMillis())
                }
        logger.info("stream available for matching: $streams")
        if (streams.isEmpty()) return emptySet()
        val usernames = setOf(
                *streamRepository.findDistinctHomeUsernamesToLowercase().toTypedArray(),
                *streamRepository.findDistinctAwayUsernamesToLowercase().toTypedArray())
        logger.info("usernames to fuzzy match in stream title: $usernames")
        return streams
                .map { stream ->
                    val relevantWordsInTitle = stream.relevantWordsInTitle(usernames)
                    val bestHomeUserMatch = relevantWordsInTitle
                            .map { word -> FuzzySearch.ratio(word, game.homeUser!!.username.toLowerCase()) }
                            .max()!!
                    val bestAwayUserMatch = relevantWordsInTitle
                            .map { word -> FuzzySearch.ratio(word, game.awayUser!!.username.toLowerCase()) }
                            .max()!!
                    Triple(stream, bestHomeUserMatch, bestAwayUserMatch).also {
                        logger.info("match result: (${it.first.title}, ${it.second} ${it.second})")
                    }
                }
                .filter { it.second >= matchThreshold && it.third >= matchThreshold }
                .map { it.first }
                .toSet()
    }

    @Transactional
    fun link(): List<Stream> =
            streamRepository.findAll()
                    .filter { it.game == null }
                    .map { Pair(it, findMatchingGame(it)) }
                    .filter { it.second != null }
                    .map { associateGame(it.first, it.second!!) }

    fun findStream(streamId: String): Optional<Stream> =
            streamRepository.findById(streamId)

    fun saveStreams(streams: Collection<Stream>): List<Stream> = streamRepository.saveAll(streams)

    fun findAll(): List<Stream> = streamRepository.findAll()

    fun findChannel(channelId: String): Optional<Channel> = channelRepository.findById(channelId)

    fun findAllChannels(): List<Channel> = channelRepository.findAll()

    fun findOne(id: String): Optional<Stream> = streamRepository.findById(id)

    fun saveChannel(channel: Channel): Channel = channelRepository.save(channel)

    fun findChannelByUsers(users: List<User>): List<Channel> = channelRepository.findAllByUserIn(users)

    fun findChannelByUser(user: User): Channel? = channelRepository.findByUser(user)

    fun saveVideoCursor(channel: Channel, videoCursor: String?): Channel =
            channelRepository.save(with (channel) { this.videoCursor = videoCursor; this})

    fun findStreams(game: Game): List<Stream> = streamRepository.findByGame(game)
}
