package com.cwtsite.cwt.game.view;

import com.cwtsite.cwt.game.entity.Game;
import com.cwtsite.cwt.game.service.GameService;
import com.cwtsite.cwt.game.view.model.GameDto;
import com.cwtsite.cwt.game.view.model.ReportDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/game")
public class GameRestController {


    private final GameService gameService;

    @Autowired
    public GameRestController(GameService gameService) {
        this.gameService = gameService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Game> reportGame(@RequestBody final ReportDto reportDto) {
        return ResponseEntity.ok(gameService.reportGame(reportDto));
    }

    @RequestMapping(value = "/many", method = RequestMethod.POST)
    public ResponseEntity<Game> reportGame(@RequestBody final List<GameDto> gameDtos) {
        throw new UnsupportedOperationException();
    }
}
