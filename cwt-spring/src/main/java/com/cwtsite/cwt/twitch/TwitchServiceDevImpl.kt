    package com.cwtsite.cwt.twitch

    import com.cwtsite.cwt.core.profile.Dev
    import com.cwtsite.cwt.twitch.model.TwitchStreamDto
    import com.cwtsite.cwt.twitch.model.TwitchVideoDto
    import org.springframework.stereotype.Service
    import java.time.LocalDateTime

    @Dev
    @Service
    class TwitchServiceDevImpl : TwitchService {
        override var lastVideosRequest: LocalDateTime? = null
        override var lastStreamsRequest: LocalDateTime? = null

        override fun requestVideos(channelIds: List<String>): List<TwitchVideoDto> {
            lastVideosRequest = LocalDateTime.now()
            return listOf(
                    TwitchVideoDto(
                            id = "1234",
                            userId = "26027047",
                            userName = "Khamski",
                            title = "CWT Finale Deluxe",
                            description = "This is the awesome CWT finale stream.",
                            createdAt = "2017-03-02T20:53:41Z",
                            publishedAt = "2017-03-03T20:53:41Z",
                            url = "https://www.twitch.tv/videos/234482848",
                            thumbnailUrl = "https://static-cdn.jtvnw.net/s3_vods/bebc8cba2926d1967418_chewiemelodies_27786761696_805342775/thumb/thumb0-%{width}x%{height}.jpg",
                            viewable = "public",
                            viewCount = 102,
                            language = "en",
                            type = "archive",
                            duration = "1h830m2s"
                    ),
                    TwitchVideoDto(
                            id = "123",
                            userId = "45204800",
                            userName = "Kayz",
                            title = "CWT Semifinale Dario Mablak 2018",
                            description = "This is just an example. I have no idea.",
                            createdAt = "2018-03-02T20:53:41Z",
                            publishedAt = "2018-03-02T20:53:41Z",
                            url = "https://www.twitch.tv/videos/234482848",
                            thumbnailUrl = "https://static-cdn.jtvnw.net/s3_vods/bebc8cba2926d1967418_chewiemelodies_27786761696_805342775/thumb/thumb0-%{width}x%{height}.jpg",
                            viewable = "public",
                            viewCount = 10,
                            language = "en",
                            type = "archive",
                            duration = "3h8m33s"
                    )
            )
        }

        override fun requestStreams(): List<TwitchStreamDto> {
            TODO("not implemented")
        }
    }
