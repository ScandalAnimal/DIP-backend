package cz.vutbr.fit.maros.dip.service;

import cz.vutbr.fit.maros.dip.model.Player;
import cz.vutbr.fit.maros.dip.model.PlayerId;
import java.util.List;

public interface PlayerService {

    Player getById(Integer id);

    List<Player> getAllPlayers();

    List<PlayerId> getAllPlayersIds();

}

