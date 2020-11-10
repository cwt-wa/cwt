package com.cwtsite.cwt.domain.stream.service

import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.stream.entity.Stream
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.domain.user.repository.entity.User
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import java.util.regex.Pattern
import kotlin.math.min

@Service
class StreamService {


    @Autowired
    private lateinit var streamRepository: StreamRepository

    @Autowired
    private lateinit var channelRepository: ChannelRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun findMatchingGame(title: String, created: String, duration: String): Pair<String?, String?> {
        val usernames = userRepository.findAllUsernamesToLowerCase()
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
        return if (res.size == 2) Pair(res[0], res[1]) else Pair(null, null)
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
