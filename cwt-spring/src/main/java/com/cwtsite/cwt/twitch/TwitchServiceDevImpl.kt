    package com.cwtsite.cwt.twitch

    import com.cwtsite.cwt.core.profile.Dev
    import com.cwtsite.cwt.domain.stream.entity.Channel
    import com.cwtsite.cwt.twitch.model.TwitchStreamDto
    import com.cwtsite.cwt.twitch.model.TwitchUserDto
    import com.cwtsite.cwt.twitch.model.TwitchVideoDto
    import org.springframework.stereotype.Service
    import java.time.LocalDateTime

    @Dev
    @Service
    class TwitchServiceDevImpl : TwitchService {
        override var lastVideosRequest: LocalDateTime? = null
        override var lastStreamsRequest: LocalDateTime? = null

        override fun requestVideos(channels: List<Channel>): List<TwitchVideoDto> {
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
                            duration = "1h43m2s"
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

        override fun requestStreams(channelIds: List<String>): List<TwitchStreamDto> =
                throw UnsupportedOperationException()

        override fun requestUsers(vararg loginNames: String): List<TwitchUserDto> {
            return listOf(
                    TwitchUserDto(
                            login = "zemkecwt",
                            viewCount = 11040,
                            id = "018054646",
                            broadcasterType = "",
                            type = "",
                            offlineImageUrl = "",
                            profileImageUrl = "https://static-cdn.jtvnw.net/jtv_user_pictures/khamski-profile_image-10dc902e62492108-300x300.jpeg",
                            description = "Mocked stream channel.",
                            displayName = "ZemkeCWT"
                    )
            )
        }
    }
