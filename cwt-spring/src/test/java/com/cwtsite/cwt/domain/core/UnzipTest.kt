package com.cwtsite.cwt.domain.core

import org.assertj.core.api.Assertions.assertThat
import java.io.File
import kotlin.test.Test

class UnzipTest {

    private val zipArchive = File(javaClass.getResource("1513.zip").toURI())

    @Test
    fun testUnzip() {
        zipArchive.inputStream().use { zipArchiveInputStream ->
            Unzip.unzipReplayFiles(zipArchiveInputStream).use { extracted ->
                assertThat(extracted.content).hasSize(4)
                assertThat(extracted.content.map { it.name }).containsExactlyInAnyOrder(
                    "2019-09-16 17.31.45 [Online Round 2] @Korydex-che, dt-saint.WAgame",
                    "2019-09-16 16.58.33 [Online] @Korydex-che, dt-saint, Fem, TdCxSenator.WAgame",
                    "2019-09-17_16.44.46_Online_Round_2_Korydex-che_StJimmy_dt-saint.WAgame",
                    "2019-09-17_17.02.45_Online_Round_3_Korydex-che_StJimmy_dt-saint.WAgame"
                )
            }
        }
    }
}
