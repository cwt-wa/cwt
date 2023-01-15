package com.cwtsite.cwt.domain.ranking.view

import com.cwtsite.cwt.domain.ranking.service.RankingService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/ranking")
class RankingRestController @Autowired
constructor(
    private val rankingService: RankingService,
) {

    @PostMapping("/calc")
    fun calc(): ResponseEntity<String> {
        val result = rankingService.relrank(rankingService.findRelevantGames())
            ?.let { rankingService.save(rankingService.findRelevantGames(), it) }
        return ResponseEntity
            .status(if (result == null) HttpStatus.CONFLICT else HttpStatus.OK)
            .build()
    }
}
