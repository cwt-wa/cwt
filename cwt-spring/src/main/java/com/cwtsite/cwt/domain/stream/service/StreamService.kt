package com.cwtsite.cwt.domain.stream.service

import com.cwtsite.cwt.domain.stream.entity.Channel
import com.cwtsite.cwt.domain.stream.entity.Stream
import com.cwtsite.cwt.domain.user.repository.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class StreamService {


    @Autowired private lateinit var streamRepository: StreamRepository
    @Autowired private lateinit var channelRepository: ChannelRepository

    fun findAll(): List<Stream> = streamRepository.findAll()

    fun findChannel(channelId: String): Optional<Channel> = channelRepository.findById(channelId)

    fun findAllChannels(): List<Channel> = channelRepository.findAll()

    fun saveStreams(streams: List<Stream>): List<Stream> = streamRepository.saveAll(streams)

    fun findOne(id: String): Optional<Stream> = streamRepository.findById(id)

    fun saveChannel(channel: Channel): Channel = channelRepository.save(channel)

    fun findChannelByUsers(users: List<User>): List<Channel> = channelRepository.findByUser(users)

    fun saveVideoCursor(channel: Channel, videoCursor: String): Channel =
            channelRepository.save(with (channel) { this.videoCursor = videoCursor; this})
}
