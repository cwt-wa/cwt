package com.cwtsite.cwt.core

import com.cwtsite.cwt.domain.stream.service.ChannelRepository
import com.cwtsite.cwt.domain.stream.service.StreamRepository
import com.cwtsite.cwt.domain.stream.service.StreamService
import com.cwtsite.cwt.domain.stream.view.model.StreamDto
import com.cwtsite.cwt.twitch.TwitchService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TwitchScheduler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val highlight = "highlight"

    @Autowired private lateinit var channelRepository: ChannelRepository
    @Autowired private lateinit var streamRepository: StreamRepository
    @Autowired private lateinit var twitchService: TwitchService
    @Autowired private lateinit var streamService: StreamService

    @Transactional
    @Scheduled(cron = "\${cwt.twitch-scheduler.cron}")
    fun schedule() {
        logger.info("Running ${this::class.simpleName}")
        streamService.cleanPaginationCursors()
        val channels = channelRepository.findAll()
        val existingVideos = streamRepository.findAll()
        val twitchVideos = twitchService.requestVideos(channels)
            .filter { it.hasCwtInTitle() }
            .map { twitchVideoDto ->
                val channel = channels.find { channel -> twitchVideoDto.userId == channel.id }
                    ?: throw RuntimeException("Channel with ID ${twitchVideoDto.userId} not found.")
                val streamDto = StreamDto.toDto(twitchVideoDto, channel)
                StreamDto.fromDto(streamDto, channel)
            }

        // Delete videos that don't exist on Twitch.
        val obsoleteVideos = existingVideos.filter { !twitchVideos.contains(it) }
        streamRepository.deleteAll(obsoleteVideos)
        logger.info("Deleted ${obsoleteVideos.size} obsolete videos")

        // Update videos from Twitch but keep the associated game if there is one.
        val updatedVideos = existingVideos
            .filter { twitchVideos.contains(it) }
            .map { it.updateTwitchInformation(twitchVideos.find { dto -> it == dto }!!) }
        logger.info("Updated ${updatedVideos.size} videos")

        // Save new videos from Twitch and associate game.
        logger.info("twitchVideos: ${twitchVideos.map { it.id }}")
        logger.info("existingVideos: ${existingVideos.map { it.id }}")
        val newVideos = twitchVideos.filter { !existingVideos.contains(it) }
        logger.info("newVideos: ${newVideos.map { it.id }}")
        newVideos
            .map { Pair(it, streamService.findMatchingGame(it)) }
            .filter { it.second != null }
            .forEach { it.first.game = it.second }
        streamRepository.saveAll(newVideos)
        logger.info("Saved ${newVideos.size} new videos")

        streamService.link()
    }
}
