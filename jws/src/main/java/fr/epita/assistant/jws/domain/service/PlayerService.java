package fr.epita.assistant.jws.domain.service;

import fr.epita.assistant.jws.data.model.Game;
import fr.epita.assistant.jws.data.model.Player;
import fr.epita.assistant.jws.domain.entity.EntPlayer;
import fr.epita.assistant.jws.domain.entity.Starting;
import fr.epita.assistant.jws.presentation.rest.request.MovePlayerRequest;
import fr.epita.assistant.jws.presentation.rest.request.PutBombRequest;
import fr.epita.assistant.jws.presentation.rest.response.GameDetailResponse;
import fr.epita.assistant.jws.presentation.rest.response.PlayerDTO;
import javax.inject.Inject;
import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static fr.epita.assistant.jws.domain.entity.Starting.*;

@ApplicationScoped
public class PlayerService {
    @Inject
    PlayerData playerData;
    @Inject GameService gameService;

    public ArrayList<Starting> Spawns = new ArrayList<Starting>(List.of(TOPLEFT, TOPRIGHT,
                                                                            BOTTOMRIGHT, BOTTOMLEFT));

    public EntPlayer createPlayer(Game gameModel, String name){
        GameDetailResponse game = gameService.getGameInfo(gameModel.id);
        int spawnIndex = game.players.size();
        Starting spawn = this.Spawns.get(spawnIndex % 4);
        long modelId = playerData.addPLayer(gameModel, name, spawn.getX(), spawn.getY(),
                                    "", "").id;
        return this.playerModelToEntity(playerData.findPlayer(modelId));
    }

    public EntPlayer playerModelToEntity(Player player){
        return new EntPlayer(player.id, player.Bomb,
                                    player.movement, player.lives,
                                    player.name, player.posX, player.posY,
                                    player.gameid, player.placeBomb,
                                    player.initPosX, player.initPosY);
    }
     public PlayerDTO PlayerEntityToDTO(EntPlayer entPlayer){
        return new PlayerDTO(entPlayer.id, entPlayer.name, entPlayer.lives, entPlayer.posX,
                            entPlayer.posY);
     }

     public boolean canPlayerMove(long pid, int tickDuration, int movementDelay){
        EntPlayer player = playerModelToEntity(playerData.findPlayer(pid));
        int minimalDelayinMs = tickDuration * movementDelay;
        if (player.lastMovement.equals("")){
            return true;
        }
        LocalDateTime lastMov = LocalDateTime.parse(player.lastMovement);
        if (lastMov.plusSeconds(minimalDelayinMs / 1000).isBefore(LocalDateTime.now())){
            return true;
        }
        return false;
     }

     public boolean isPlayerInGame(GameDetailResponse game, long playerId){
        for (PlayerDTO p: game.players){
            if (p.id == playerId){
                return true;
            }
        }
        return false;
     }

     public boolean isPlayerDead(long playerId){
        EntPlayer player = playerModelToEntity(playerData.findPlayer(playerId));
        if (player.lives > 0){
            return false;
        }
        return true;
     }

     public PlayerDTO findPlayerinGame(GameDetailResponse game, long playerId){
        for (PlayerDTO player: game.players){
            if (player.id == playerId){
                return player;
            }
        }
        return null;
     }

     public boolean isDirectionCardinal(MovePlayerRequest request, long playerId){
        EntPlayer player = this.playerModelToEntity(playerData.findPlayer(playerId));
        if (request.posX == player.posX + 1 && request.posY == player.posY){
            return true;
        }
        else if (request.posX == player.posX - 1 && request.posY == player.posY){
             return true;
        }
        else if (request.posX == player.posX  && request.posY == player.posY + 1){
             return true;
        }
        else if (request.posX == player.posX  && request.posY == player.posY - 1){
             return true;
        }
        return false;
     }

     public void movePlayer(int posx, int posy, long playerId){
        EntPlayer player = playerModelToEntity(playerData.findPlayer(playerId));
        player.posX = posx;
        player.posY = posy;
        player.lastMovement = LocalDateTime.now().toString();
        playerData.modifyPlayer(player);
     }

     public boolean canPlayerPlaceBomb(long playerId, int tickDuration, int bombDelay,
                                      PutBombRequest request){
        EntPlayer player = this.playerModelToEntity(playerData.findPlayer(playerId));
        if (this.isPlayerDead(player.id)){
            return false;
        }
        if (request.posX != player.posX || request.posY != player.posY){
            return false;
        }
        if (player.lastMovement.equals("")){
            return true;
        }
        return player.canPlaceBombs;
     }
}
