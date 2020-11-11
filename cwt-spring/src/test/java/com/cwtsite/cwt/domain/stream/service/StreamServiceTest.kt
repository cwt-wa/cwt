package com.cwtsite.cwt.domain.stream.service

import com.cwtsite.cwt.domain.game.entity.PlayoffGame
import com.cwtsite.cwt.domain.game.service.GameRepository
import com.cwtsite.cwt.domain.stream.entity.Stream
import com.cwtsite.cwt.domain.tournament.entity.enumeration.TournamentStatus
import com.cwtsite.cwt.domain.tournament.service.TournamentService
import com.cwtsite.cwt.domain.user.repository.UserRepository
import com.cwtsite.cwt.test.EntityDefaults
import org.assertj.core.api.Assertions.assertThat
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.Test

@RunWith(MockitoJUnitRunner::class)
class StreamServiceTest {

    @InjectMocks private lateinit var cut: StreamService
    @Mock private lateinit var streamRepository: StreamRepository
    @Mock private lateinit var channelRepository: ChannelRepository
    @Mock private lateinit var userRepository: UserRepository
    @Mock private lateinit var tournamentService: TournamentService
    @Mock private lateinit var gameRepository: GameRepository

    @Test
    fun findMatchingGame() {
        val testset = listOf(
                Pair(Pair("Master", "tita"), Triple("CWT'16 | PLAYOFFS | 1/4 STAGE | MASTER - TITA [EPISODE 1]", "2016-11-28T22:15:42Z", "1h2m6s")),
                Pair(Pair("tadeusz", "chuvash"), Triple("CWT'16 | PLAYOFFS | 1/4 STAGE | TADEUSZ - CHUVASH", "2016-11-26T22:06:35Z", "1h16m57s")),
                Pair(Pair("nickynick", "aladdin"), Triple("CWT'18 | GROUP B | NICKYNICK - ALLADIN", "2018-11-01T22:33:35Z", "2h49m47s")),
                Pair(Pair("Tade", "psykologi"), Triple("CWT'20 | GROUP E | TADE  - PSYKOLOGI", "2020-10-08T15:33:10Z", "2h3m34s")),
                Pair(Pair("FaD", "Fantomas"), Triple("CWT 2015 Bronze match: FaD vs Fantomas", "2015-12-20T19:57:41Z", "1h53m36s")),
                Pair(Pair("KinslayeR", "chuvash"), Triple("CWT'16 |  PLAYOFFS | 1/8 STAGE| KINSLAYER - CHUVASH", "2016-11-16T23:39:01Z", "1h33m14s")),
                Pair(Pair("Tade", "Mablak"), Triple("CWT'16 | PLAYOFFS | SEMI FINAL | TADE - MABLAK", "2016-12-06T23:13:07Z", "2h30m58s")),
                Pair(Pair("TerRoR", "Kayz"), Triple("CWT'14. Fight for bronze. TerRor - Kayz", "2014-12-23T13:03:36Z", "3h12m38s")),
                Pair(Pair("Fantomas", "Kayz"), Triple("CWT'16 | PLAYOFFS | 1/8 STAGE | FANTOMAS - KAYZ", "2016-11-24T21:52:22Z", "2h27m56s")),
                Pair(Pair("Kayz", "Thouson"), Triple("CWT | Kayz Vs Thouson | Silent Stream", "2020-09-23T02:26:03Z", "1h35m46s")),
                Pair(Pair("Kayz", "Abegod"), Triple("CWT 2020 - Playoffs | Kayz vs. Abegod | Intermediate | TTS - !say", "2020-10-29T20:04:15Z", "1h30m59s")),
                Pair(Pair("chuvash", "tita"), Triple("CWT'14. Round of 8. chuvash - tita. [part 1]", "2014-11-22T10:37:24Z", "1h50m14s")),
                Pair(Pair("TerRoR", "nickynick"), Triple("CWT'14. Round of 8. Terror - NickyNick. [part 1]", "2014-11-22T10:46:59Z", "1h50m19s")),
                Pair(Pair("Johnmir", "viks"), Triple("Replay analysis [russian]. CWT'14. Last 16. Johnmir - Viks.", "2014-11-22T10:50:31Z", "49m41s")),
                Pair(Pair("Johnmir", "viks"), Triple("CWT'14. Last 16. Johnmir - Viks.", "2014-11-22T10:49:14Z", "1h24m14s")),
                Pair(Pair("TerRoR", "chuvash"), Triple("CWT'14. Semi-final. Terror - chuvash", "2014-11-25T16:46:34Z", "2h12m8s")),
                Pair(Pair("tita", "Jellenio"), Triple(" CWT 2016 tita vs. Jellenio ", "2016-10-31T11:24:13Z", "50m41s")),
                Pair(Pair("TerRoR", "nickynick"), Triple("CWT'14. Round of 8. Terror - NickyNick. [part 2]", "2014-11-22T10:47:30Z", "1h29m16s")),
                Pair(Pair("FaD", "Tomek"), Triple("CWT'14. Round of 16. FaD - Tomek [part 1]", "2014-11-22T10:09:53Z", "1h30m20s")),
                Pair(Pair("Dario", "Mablak"), Triple("CWT 2011. Semifinal. Dario - Mablak. ", "2011-11-14T19:48:57Z", "1h27m44s")),
                Pair(Pair("chuvash", "tita"), Triple("CWT'14. Round of 8. chuvash - tita. [part 2]", "2014-11-22T10:38:06Z", "1h33m0s")),
                Pair(Pair("nickynick", "kilobyte"), Triple("CWT 2014 nickynick vs. Kilobyte", "2014-10-26T21:54:12Z", "55m49s")),
                Pair(Pair("tanerrr", "Boolc"), Triple("CWT 2015 taner vs. BoolC", "2015-10-16T17:57:18Z", "2h17m1s")),
                Pair(Pair("lacoste", "SiD"), Triple("CWT 2020 | lacoste Vs SiD | Intermediate | TTS - !say", "2020-10-20T17:13:01Z", "1h43m38s")),
                Pair(Pair("tanerrr", "StJimmy"), Triple("CWT 2020 | taner Vs StJimmy | Group Stage | TTS - !say", "2020-10-12T20:11:02Z", "1h56m59s")),
                Pair(Pair("chuvash", "kalababa"), Triple("CWT 2015 chuvash vs. kalababa", "2015-10-14T19:04:48Z", "59m18s")),
                Pair(Pair("nickynick", "Gopnick"), Triple("CWT 2015 nickynick vs. GopnicK", "2015-10-19T19:59:53Z", "58m52s")),
                Pair(Pair("Random00", "Chilitolindo"), Triple("CWT 2014 Playoff Random00 vs. Chilitolindo", "2014-11-03T23:17:02Z", "3h7m4s")),
                Pair(Pair("Johnmir", "FaD"), Triple("CWT 2014 Quarterfinal Johnmir vs. FaD", "2014-11-30T17:34:36Z", "2h22m58s")),
                Pair(Pair("chuvash", "khamski"), Triple("CWT 2015 chuvash vs. khamski", "2015-11-03T15:36:08Z", "1h22m16s")),
                Pair(Pair("TerRoR", "nickynick"), Triple("CWT 2014 TerRoR vs. nickynick", "2014-10-19T22:28:38Z", "1h58m9s")),
                Pair(Pair("khamski", "nickynick"), Triple(" CWT 2014 Playoff Khamski vs. nickynick ", "2014-11-09T21:13:37Z", "2h7m4s")),
                Pair(Pair("tita", "Tade"), Triple("CWT 2014 Playoff tita vs. Tade", "2014-11-10T00:10:05Z", "2h22m54s")),
                Pair(Pair("chuvash", "korydex"), Triple("CWT 2014 Playoff chuvash vs. Korydex ", "2014-11-02T20:54:22Z", "56m14s")),
                Pair(Pair("tita", "Master"), Triple("CWT 2016, Quarter Final, Tita vs Master (conclusion)", "2016-12-09T18:53:04Z", "47m10s")),
                Pair(Pair("Siwy", "tanerrr"), Triple("CWT 2018, Group A, Siwy vs Taner", "2018-11-18T21:18:58Z", "1h41m26s")),
                Pair(Pair(null, null), Triple("CWT'15 Groups | Games review [russian] ", "2015-10-22T21:25:32Z", "1h23m30s")),
                Pair(Pair("Fenrys", "Senator"), Triple("CWT 2017, Group D, Fenrys vs Senator", "2017-11-16T20:30:37Z", "1h21m59s")),
                Pair(Pair("chuvash", "Boolc"), Triple("CWT 2018, Semi Final, Chuvash vs Boolc", "2019-02-14T21:42:33Z", "1h16m35s")),
                Pair(Pair("kano", "khamski"), Triple("CWT 2020 - Playoffs | Kano vs. khamski | Intermediate | TTS - !say", "2020-10-31T18:02:14Z", "2h6m23s")),
                Pair(Pair("zoky", "kalababa"), Triple("zoky vs. kalababa CWT 2013", "2013-10-14T20:56:22Z", "1h12m54s")),
                Pair(Pair("FaD", "Peja"), Triple("CWT 2014 FaD vs. Peja", "2014-10-14T19:20:07Z", "1h42m1s")),
//                Pair(Pair("Piki1802", "Siwy"), Triple("CWT 2018, Semi Final, Piki vs Siwy (w/o last round)", "2019-02-23T09:06:29Z", "1h40m42s")),
                Pair(Pair("chuvash", "khamski"), Triple("chuvash vs. Khamski CWT 2013", "2013-10-28T18:31:03Z", "1h22m27s")),
                Pair(Pair("chuvash", "Mablak"), Triple("CWT'16 | FINAL | CHUVASH - MABLAK", "2016-12-19T00:58:23Z", "5h19m18s")),
                Pair(Pair("chuvash", "Siwy"), Triple("CWT '18 | chuvash vs. Siwy | Finals | Commentators: MrTPenguin, Dr. Abegod, Sensei | Worms Armageddon", "2019-03-02T21:52:19Z", "2h25m47s")),
                Pair(Pair("tita", "Boolc"), Triple("CWT 2016, Group B, tita vs Boolc", "2016-11-05T22:11:29Z", "1h37m58s")),
                Pair(Pair("nickynick", "tita"), Triple("CWT 2020 - Playoffs | nickynick vs. tita | Intermediate | TTS - !say", "2020-11-01T19:33:14Z", "1h28m10s")),
                Pair(Pair("Kayz", "Akt"), Triple("CWT'13. Group B. Kayz - Akt. [part 1]", "2013-11-12T09:31:13Z", "9m2s")),
                Pair(Pair("Rafka", "Siwy"), Triple("CWT 2019, Final, Rafka vs Siwy, part two", "2020-01-09T16:05:22Z", "1h20m43s")),
                Pair(Pair("Kayz", "Akt"), Triple("CWT'13. Group B. Kayz - Akt. [part 2]", "2013-11-12T09:33:09Z", "1h22m57s")),
                Pair(Pair(null, null), Triple("CWT'14. The Grand Finale.", "2014-12-23T22:38:10Z", "3h17m17s")),
                Pair(Pair("Rafka", "Siwy"), Triple("CWT 2019, Final, Rafka vs Siwy, part one", "2020-01-09T15:56:58Z", "1h47m35s")),
                Pair(Pair("Joschi", "Koras"), Triple("CWT 2011. Semifinal. Joschi - Koras.", "2011-11-14T19:57:30Z", "1h34m5s")),
                Pair(Pair("PavelB", "Thouson"), Triple("CWT 2020 - Playoffs | PavelB vs. Thouson | Intermediate | TTS - !say", "2020-10-24T15:58:58Z", "2h33m52s")),
                Pair(Pair("Fantomas", "Tade"), Triple("CWT 2015 Fantomas vs. Tade", "2015-11-18T19:48:02Z", "1h45m48s")),
                Pair(Pair("tanerrr", "DarKxXxLorD"), Triple("CWT'16 | GROUP A | taner - Darklord", "2016-10-29T17:21:36Z", "2h11m54s")),
                Pair(Pair("Kayz", "Siwy"), Triple("CWT '18 | Kayz vs. Siwy | Quarter-Finals | Commentators: Dr. Abegod, Sensei | Worms Armageddon", "2019-02-06T23:44:56Z", "2h15m25s")),
                Pair(Pair("Rafka", "PavelB"), Triple("CWT 2020 - Playoffs (Quarter Finals) | Rafka Vs PavelB | Intermediate Scheme | TTS - !say", "2020-11-08T11:54:49Z", "3h15m9s")),
                Pair(Pair("tita", "pavlepavle"), Triple("CWT '18 | tita vs pavlepavle | Last 16 | Commentators: TheWalrus, taner", "2019-01-11T20:40:53Z", "1h22m19s")),
                Pair(Pair("Kayz", "zoky"), Triple("CWT'14. PlayOffs. Kayz - zoky", "2014-11-04T22:55:22Z", "1h47m35s")),
                Pair(Pair("chuvash", "Akt"), Triple("CWT 2014 chuvash vs. Akt", "2014-10-14T19:25:58Z", "1h1m51s")),
                Pair(Pair("tadeusz", "Boolc"), Triple("CWT 2016, 1/8 Round, tadeusz vs Boolc", "2016-11-18T21:25:27Z", "1h20m3s")),
                Pair(Pair("FaD", "Tomek"), Triple("CWT'14. Round of 16. FaD - Tomek [part 2]", "2014-11-22T10:12:56Z", "1h40m6s")),
                Pair(Pair("Bytor", "Tade"), Triple("CWT'16 | GROUP E | BYTOR - TADE", "2016-11-10T20:53:37Z", "57m31s")),
                Pair(Pair("chuvash", "Piki1802"), Triple("CWT'15. PlayOffs. Last 16. chuvash - Piki1802", "2015-11-12T20:16:27Z", "55m57s")),
                Pair(Pair("nickynick", "Bytor"), Triple("CWT 2016, Group E, nickynick vs Bytor", "2016-10-30T19:49:43Z", "1h5m50s")),
//                Pair(Pair("nickynick", "Free"), Triple("CWT'16 | PLAYOFFS | 1 / 8 STAGE | NICKY NICK - FREE", "2016-11-20T20:59:43Z", "1h32m54s")),
                Pair(Pair("FaD", "Kayz"), Triple("CWT'14. Semi-final. FaD - Kayz", "2014-12-09T10:02:40Z", "1h46m3s")),
                Pair(Pair("nickynick", "Kano"), Triple("CWT 2015, Knockouts, NickyNick vs Kano", "2015-11-14T18:17:23Z", "1h17m28s")),
                Pair(Pair("tita", "chuvash"), Triple("CWT 2016 tita vs. chuvash Semifinal", "2016-12-14T21:01:06Z", "1h51m24s")),
                Pair(Pair("Kayz", "Random00"), Triple("CWT'14. Quarter-finals. Kayz - Random00.", "2014-11-13T20:18:33Z", "1h24m30s")),
                Pair(Pair("Afina", "Fantomas"), Triple("CWT'16 | GROUP A | AFINA - FANTOMAS", "2016-11-06T23:16:38Z", "2h8m25s")),
                Pair(Pair("tita", "CoolMan"), Triple("CWT 2015, Group E, Tita vs CoolMan, Round 1", "2015-10-24T20:00:21Z", "31m56s")),
                Pair(Pair("Mablak", "viks"), Triple("CWT'16 | GROUP D | Mablak - Viks", "2016-10-30T23:06:41Z", "1h38m24s")),
                Pair(Pair("tita", "tanerrr"), Triple("CWT'16 |  PLAYOFFS | 1/8 STAGE| TITA - TANER", "2016-11-16T23:35:47Z", "2h13m38s")),
//                Pair(Pair("tanerrr", "Akt"), Triple("CWT 2015 MIGHTYtaner vs. AKT", "2015-11-11T21:50:33Z", "1h39m47s")),
                Pair(Pair("Random00", "lacoste"), Triple("CWT 2014 Random00 vs. Lacoste", "2014-10-27T20:49:09Z", "1h25m40s")),
                Pair(Pair("FaD", "tanerrr"), Triple("CWT 2015 FaD vs. taner", "2015-11-22T21:34:15Z", "1h53m34s")),
                Pair(Pair("TerRoR", "lacoste"), Triple("CWT 2014 Playoff TerRoR vs. lacoste ", "2014-11-08T20:47:46Z", "1h45m19s")),
                Pair(Pair("Bytor", "FaD"), Triple("CWT 2014 Bytor vs. FaD", "2014-10-19T19:28:18Z", "1h0m34s")),
                Pair(Pair("khamski", "FaD"), Triple("CWT 2014 Khamski vs. FaD", "2014-10-26T21:59:20Z", "1h27m29s")),
                Pair(Pair("chuvash", "Tade"), Triple("CWT 2014 chuvash vs. Tade", "2014-10-21T20:26:51Z", "1h52m52s")),
                Pair(Pair("chuvash", "Kayz"), Triple("CWT 2015 finals: chuvash vs Kayz", "2015-12-22T22:37:51Z", "2h46m22s")),
                Pair(Pair("khamski", "Bytor"), Triple("CWT 2014 Khamski vs. Bytor", "2014-10-30T22:56:45Z", "1h38m52s")),
                Pair(Pair("TerRoR", "kilobyte"), Triple("CWT 2014 TerRor vs. Kilobyte", "2014-10-18T22:28:17Z", "1h58m6s")),
                Pair(Pair("WorldMaster", "khamski"), Triple("CWT 2015 WorldMaster vs. Khamski", "2015-11-01T23:16:55Z", "1h39m4s")),
                Pair(Pair("FaD", "Kayz"), Triple("CWT 2015 SF: FaD vs Kayz", "2015-12-08T09:53:37Z", "2h53m9s")),
                Pair(Pair("kilobyte", "Asbest"), Triple("CWT 2016 Qualifiers Kilobyte vs. Asbest", "2016-10-11T21:12:16Z", "1h59m52s")),
                Pair(Pair("FaD", "Jakka"), Triple("CWT 2015 FaD vs. Jakk0", "2015-11-22T21:36:46Z", "1h13m10s")))
        val usernames = listOf(
                "k4tsis", "jellenio", "kayz", "mablak", "solomon", "thewormsplayer", "johnmir", "bytor", "c00l", "koras", "darkxxxlord",
                "kano", "dario", "eru777", "tade", "maze", "mtorpey", "crespo", "khamski", "rafka", "joschi", "domi", "alex13", "crvic",
                "muzer", "zolodu", "korydex", "worldmaster", "terror", "phantom", "dreamtrance", "jigsaw", "kinslayer", "antares", "lales",
                "sibasa", "jule", "thurbo", "yungiz", "run", "pandello", "betongasna", "albino", "fantomas", "normalpro", "sirgorash",
                "retcerahc", "rigga", "pmgrnt", "rusher", "lacoste", "kalababa", "lovevilution", "dawgystyle", "snakeplissken", "aymen",
                "diablovt", "free", "weem4n", "lait88", "ultiartsy", "perdunok", "srpanther", "gingerplusplus", "ypslon", "thouson", "komito",
                "kebniak", "tita", "kinggizard", "cnb", "kukumber", "kba3u", "pizzasheet", "casso", "gabriel", "barman", "crazy", "dmitry",
                "rafalus", "ramone", "spike", "tomt", "chelsea", "piki1802", "husk", "ducky", "krd", "bl4st3r", "phanton", "felo", "kungpow",
                "jakka", "triad", "floydmilleraustr", "adun", "abegod", "szoszo", "abnaxus", "eray", "ilyxa", "pavelb", "zemke", "tanerrr",
                "sainto", "mrtpenguin", "sensei", "ljungberg", "lostboy ", "luisinho", "leoric", "makimura", "mexicangeneral", "mbonheur",
                "mielu", "manolo", "m0nk", "majesticjara", "mastertool", "misirac", "mattekale", "nut", "nachwein", "optymus", "oz",
                "papabizkitpark", "pitbacardi", "phartknocker", "paranoico", "petazeta", "philippo", "raven", "simon", "saibot", "semaphore",
                "salda", "sniper", "tixas", "teletubbies", "trixsk8", "tomek", "hwoarangcs", "viks", "kilobyte", "oscardianno", "theextremist",
                "sigmatel", "zoky", "chicken23", "psykologi", "ivo", "nickynick", "fonseca", "waffle", "stripe", "gopnick", "random00", "cinek",
                "haxen", "master", "ratoonsoft", "chuvash", "fad", "littlebiatch", "fenrys", "xdragonfirex", "dsa", "pipinasso", "ash", "akkad",
                "ashmallum", "automatico", "aquaworm", "chaos1187", "chew", "clay", "cool", "crimsonscourge", "drakken", "darasek", "dustedtrooper",
                "dani", "david", "dvorkin", "dan", "doubletime", "euenahub", "eebu", "feiticeiro", "fury", "franpetas", "gelete", "generalhorn", "goku",
                "hhc", "gforce", "hallq", "hatequitter", "intrepid", "inspire", "javito", "jessbaec", "konrad", "kortren", "kisiro", "linusb",
                "urbanspaceman", "uncledave", "ucantseeme", "ukrop", "voodoo", "vodkoff", "vuk", "wormsik", "wriggler", "xaositect", "xworm",
                "zero", "hussar", "franz", "peja", "caniman", "letotalkiller", "uzurpator", "jago", "sbaffo", "kyho", "goro", "wormslayer",
                "tajniak", "superpipo", "j0hny", "13", "kain", "worminggirl", "marjano", "pellefot", "evito", "tadeusz", "sid", "noox", "darkone",
                "henrycs", "vesuvio", "chilitolindo", "vok", "steps", "combo", "zexorz", "spyvsspyfan123", "thewalrus", "asbest", "kins", "coolman",
                "afina", "atr0x", "vok90", "daina", "street", "instantly", "whiterqbbit", "dvd", "tadeuszek", "siwy", "pavlepavle", "akt", "hldd3n",
                "albus", "knighttemplar", "vojske", "kaleu", "artecthefox", "megaadnan", "goosey", "bhaal", "thyx", "nunca", "nous", "kievz", "stjimmy",
                "boolc", "aladdin", "fusi", "silaneo", "kingkong", "stoner", "senator", "alwer939")
        val toStream = { title: String -> Stream(title = title, id = 1, viewCount = 2, channel = EntityDefaults.channel()) }
        val tournament = EntityDefaults.tournament(status = TournamentStatus.PLAYOFFS)
        `when`(tournamentService.getCurrentTournament())
                .thenReturn(tournament)
        `when`(gameRepository.findDistinctHomeUsernamesToLowercaseInPlayoffs(tournament))
                .thenReturn(usernames.subList(0, 30))
        `when`(gameRepository.findDistinctAwayUsernamesToLowercaseInPlayoffs(tournament))
                .thenReturn(usernames.subList(30, usernames.size))
        testset.forEach { set ->
            if (set.first.first == null || set.first.second == null) {
                assertThat(cut.findMatchingGame(toStream(set.second.first))).isNull()
                return@forEach
            }
            val user1 = EntityDefaults.user(username = set.first.first!!)
            val user2 = EntityDefaults.user(username = set.first.second!!)
            `when`(userRepository.findByUsernameIgnoreCase(anyString())).thenReturn(user1).thenReturn(user2)
            val game = EntityDefaults.game(homeUser = user1, awayUser = user2, playoff = PlayoffGame(1, 1, 1))
            `when`(gameRepository.findGame(user1, user2, tournament)).thenReturn(listOf(game))
            assertThat(cut.findMatchingGame(toStream(set.second.first)))
                    .satisfies { matchingGame ->
                        assertThat(matchingGame).isNotNull
                        assertThat(matchingGame).satisfiesAnyOf({
                            assertThat(it!!.homeUser).isEqualTo(user1)
                            assertThat(it.awayUser).isEqualTo(user2)
                        }, {
                            assertThat(it!!.homeUser).isEqualTo(user2)
                            assertThat(it.awayUser).isEqualTo(user1)
                        })
                    }
        }
    }
}
