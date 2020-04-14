package cz.vutbr.fit.maros.dip.controller;

import cz.vutbr.fit.maros.dip.constants.ApiConstants;
import cz.vutbr.fit.maros.dip.model.Player;
import cz.vutbr.fit.maros.dip.model.PlayerId;
import cz.vutbr.fit.maros.dip.results.ResponseWrapper;
import cz.vutbr.fit.maros.dip.service.impl.PlayerServiceImpl;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/player")
public class PlayerController {

    private final PlayerServiceImpl playerService;

    public PlayerController(PlayerServiceImpl playerService) {
        this.playerService = playerService;
    }

    @GetMapping(value = "/{id}")
    public ResponseWrapper<Player> getPlayerById(
            @Valid @Pattern(regexp = ApiConstants.REGEX_FOR_NUMBERS, message = ApiConstants.MESSAGE_FOR_REGEX_NUMBER_MISMATCH) @PathVariable(value = "id")
                    String id) {
        return new ResponseWrapper<>(playerService.getById(Integer.parseInt(id)), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseWrapper<List<Player>> getAllPlayers() {
        return new ResponseWrapper<>(playerService.getAllPlayers(), HttpStatus.OK);
    }

    @GetMapping(value = "/ids")
    public ResponseWrapper<List<PlayerId>> getAllPlayerIds() {
        return new ResponseWrapper<>(playerService.getAllPlayersIds(), HttpStatus.OK);
    }

}
