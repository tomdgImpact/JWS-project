package fr.epita.assistant.jws.domain.service;

import fr.epita.assistant.jws.data.model.Game;
import fr.epita.assistant.jws.data.model.Player;
import fr.epita.assistant.jws.domain.entity.EntPlayer;

import javax.inject.Inject;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class PlayerData {
    @Inject
    GameData gameData;

    @Transactional public Player addPLayer(Game game, String name, int x, int y, String bomb, String mov){
        Player player = new Player()
                                            .withName(name)
                                            .withLives(3)
                                            .withPosX(x)
                                            .withPosY(y).withGameid(game.id)
                                            .withBomb(bomb)
                                            .withMovement(mov)
                                            .withPlaceBomb(true)
                                            .withInitPosX(x)
                                            .withInitPosY(y);
        Player.persist(player);
        return player;
    }

    public Set<Player> getAllPlayers() {
        return Player.<Player>findAll().stream().collect(Collectors.toSet());
    }

    @Transactional
    public Player removePlayer(long givenId) {
        Player res = Player.<Player>findAll().stream().filter(player -> player.id == givenId).findFirst().orElse(null);
        Game.deleteById(givenId);
        return res;
    }

    @Transactional
    public Player findPlayer(long givenId) {
        Player player = Player.<Player>findAll().stream().filter(play -> play.id == givenId).findFirst().orElse(null);
        return player;
    }

    @Transactional
    public Set<Player> findPlayersFromGame(long givenId) {
        Set<Player> player = Player.<Player>findAll().stream().filter(play -> play.gameid == givenId).collect(Collectors.toSet());
        return player;
    }

    @Transactional
    public Player modifyPlayer(EntPlayer entPlayer){
        Player player = Player.findById(entPlayer.id);
        player.Bomb = entPlayer.lastBomb;
        player.movement = entPlayer.lastMovement;
        player.posX = entPlayer.posX;
        player.posY = entPlayer.posY;
        player.lives = entPlayer.lives;
        player.placeBomb = entPlayer.canPlaceBombs;
        return Player.findById(entPlayer.id);
    }


}
