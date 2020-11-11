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
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import java.util.regex.Pattern

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

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun findMatchingGame(title: String, created: String, duration: String): Game? {
        val tournament = tournamentService.getCurrentTournament()
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
        val blacklist = setOf(
                "cwt", "final", "finale", "quarter", "semi", "quarterfinal", "semifinal", "last",
                "group", "stage", "playoff", "playoffs", "vs", "round", "of", "-", "part", "round")
                .filter { !usernames.contains(it) }
        val split = title.toLowerCase().split(Pattern.compile("\\W"))
                .asSequence()
                .filter { !blacklist.contains(it) }
                .filter { it.length >= 3 }
                .filter { !Regex("\\d{4}").matches(it) }
                .filter { it.contains(Regex("\\w")) }
                .map { it.trim() }
                .toList()
        val res = split
                .asSequence()
                .map { word ->
                    usernames
                            .map { username -> Pair(username, FuzzySearch.ratio(username, word)) }
                            .maxBy { it.second }!!
                }
                .filter { it.second >= 70 }
                .sortedByDescending { it.second }
                .take(2)
                .map { it.first }
                .toList()
        if (res.size != 2) return null
        val user1 = userRepository.findByUsernameIgnoreCase(res[0])
        val user2 = userRepository.findByUsernameIgnoreCase(res[1])
        val potentialGames = when (tournament) {
            null -> gameRepository.findGame(user1!!, user2!!)
            else -> gameRepository.findGame(user1!!, user2!!, tournament)
        }
        return potentialGames
                .filter {
                    when (tournament?.status) {
                        TournamentStatus.GROUP -> it.playoff == null
                        TournamentStatus.PLAYOFFS -> it.playoff != null
                        else -> true
                    }
                }
                .maxBy { it.created!! }
    }

    fun findAll(): List<Stream> = streamRepository.findAll()

    fun findChannel(channelId: String): Optional<Channel> = channelRepository.findById(channelId)

    fun findAllChannels(): List<Channel> = channelRepository.findAll()

    fun saveStreams(streams: List<Stream>): List<Stream> = streamRepository.saveAll(streams)

    fun findOne(id: String): Optional<Stream> = streamRepository.findById(id)

    fun saveChannel(channel: Channel): Channel = channelRepository.save(channel)

    fun findChannelByUsers(users: List<User>): List<Channel> = channelRepository.findAllByUserIn(users)

    fun saveVideoCursor(channel: Channel, videoCursor: String?): Channel =
            channelRepository.save(with (channel) { this.videoCursor = videoCursor; this})
}
