package fr.epita.assistant.jws.domain.service;

import fr.epita.assistant.jws.data.model.Game;
import fr.epita.assistant.jws.data.model.Player;
import fr.epita.assistant.jws.domain.entity.EntGameEnt;
import fr.epita.assistant.jws.domain.entity.EntPlayer;
import fr.epita.assistant.jws.presentation.rest.request.MovePlayerRequest;
import fr.epita.assistant.jws.presentation.rest.request.PutBombRequest;
import fr.epita.assistant.jws.presentation.rest.response.*;
import javax.inject.Inject;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.epita.assistant.jws.presentation.rest.response.GameState.*;

@ApplicationScoped
public class GameService {

    @Inject
    GameData gameData;
    @Inject PlayerService playerService;
    @Inject
    PlayerData playerData;

    public static ArrayList<String> mapConverter(String map){
        ArrayList<String> result = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (char c: map.toCharArray()){
            sb.append(c);
            count++;
            if (count == 17){
                count = 0;
                result.add(sb.toString());
                sb = new StringBuilder();
            }
        }
        return result;
    }

    public static String mapToString(ArrayList<String> map){
        return String.join("", map);
    }

    public EntGameEnt gameModelToEntity(Game game){
        GameState state;
        if (game.status.equals("RUNNING"))
            state = RUNNING;
        else if (game.status.equals("STARTING"))
            state = STARTING;
        else
            state = FINISHED;
        ArrayList<String> entityMap = (ArrayList<String>) mapConverter(game.map).stream().map(s -> MapProcessing.RLEencode(s)).collect(Collectors.toList());
        EntGameEnt res = new EntGameEnt(state, game.id, new HashSet<EntPlayer>(), game.start, entityMap);
        Set<Player> players = playerData.findPlayersFromGame(res.id);
        players.stream().forEach(model -> res.players.add(playerService.playerModelToEntity(model)));
        return res;
    }

    public GameResponse gameEntityToGameResponse(EntGameEnt entGameEnt){
        return new GameResponse(entGameEnt.id, entGameEnt.players.size(), entGameEnt.state.toString());
    }

    public GameDetailResponse entityToDetailResponse(EntGameEnt entGameEnt){
        String detailDate = entGameEnt.startTime.toString().substring(0, 24);
        GameDetailResponse res = new GameDetailResponse(detailDate, entGameEnt.state.toString(), new HashSet<PlayerDTO>(), entGameEnt.map, entGameEnt.id);
        entGameEnt.players.stream().forEach(player -> res.players.add(playerService.PlayerEntityToDTO(player)));
        return res;
    }

    public GameListResponse gameListResponse(){
        Set<Game> allGames = gameData.getAllGames();
        if (allGames == null){
            return new GameListResponse();
        }
        GameListResponse res = new GameListResponse();
        allGames
                .stream()
                .forEach(i -> res.addGame(gameEntityToGameResponse(gameModelToEntity(i))));
        return res;
    }

    public EntGameEnt createGame(String map, String playerName){
        long modelId = gameData.addGame(map, STARTING, LocalDateTime.now()).id;
        playerService.createPlayer(gameData.findGame(modelId), playerName);
        EntGameEnt entGameEnt = this.gameModelToEntity(gameData.findGame(modelId));
        return entGameEnt;
    }

    public GameDetailResponse getGameInfo(long id){
        Game game = gameData.findGame(id);
        if (game == null){
            return null;
        }
        return entityToDetailResponse(gameModelToEntity(game));
    }

    public GameDetailResponse joinGame(long gameId, String playerName){
        playerService.createPlayer(gameData.findGame(gameId), playerName);
        return getGameInfo(gameId);
    }

    public Game modifyGame(GameState state, long id){
        gameData.modifyGame(id, state);
        return gameData.findGame(id);
    }

    public boolean isValidPoint(int x, int y, Long gameid){
        Game game = gameData.findGame(gameid);
        if (x < 0 || x > 16 || y < 0 || y > 14){
            return false;
        }
        ArrayList<String> map = this.mapConverter(game.map);
        char c = map.get(y).charAt(x);
        if (c == 'G'){
            return true;
        }
        return false;
    }

    public boolean isGameFinished(long gameId){
        EntGameEnt game = this.gameModelToEntity(gameData.findGame(gameId));
        int count = 0;
        for (var p : game.players){
            if (!playerService.isPlayerDead(p.id)){
                count++;
            }
        }
        return count <= 1;
    }

    public GameDetailResponse finishGame(long gameId){
        if (isGameFinished(gameId))
            modifyGame(FINISHED, gameId);
        return getGameInfo(gameId);
    }

    public GameDetailResponse putBomb(long gameId, PutBombRequest request, int playerId){
        Game game = gameData.findGame(gameId);
        ArrayList<String> map = (ArrayList<String>) this.mapConverter(game.map).stream()
                        .map(s -> MapProcessing.RLEdecode(s)).collect(Collectors.toList());
        String s = map.get(request.posY);
        StringBuilder sb = new StringBuilder(s);
        sb.setCharAt(request.posX, 'B');
        map.set(request.posY, sb.toString());
        gameData.modifyGameMap(gameId, mapToString(map));
        EntPlayer player = playerService.playerModelToEntity(playerData.findPlayer(playerId));
        player.lastBomb = LocalDateTime.now().toString();
        player.canPlaceBombs = false;
        playerData.modifyPlayer(player);
        return getGameInfo(gameId);
    }

    public void explodeBomb(long gameId, int tickDuration, int delayBomb,
                                                PutBombRequest request, long playerId){
        Game game = gameData.findGame(gameId);
        ArrayList<String> map = mapConverter(game.map);
        EntGameEnt entGameEnt = this.gameModelToEntity(game);
        PutBombRequest point1 = new PutBombRequest(request.posX - 1, request.posY);
        PutBombRequest point2 = new PutBombRequest(request.posX + 1, request.posY);
        PutBombRequest point3 = new PutBombRequest(request.posX, request.posY + 1);
        PutBombRequest point4 = new PutBombRequest(request.posX, request.posY - 1);
        List<PutBombRequest> points = List.of(point1, point2, point3, point4);
        try {
            Thread.sleep(tickDuration * delayBomb);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (var point: points){
            char c = map.get(point.posY).charAt(point.posX);
            if (c == 'W'){
                StringBuilder sb = new StringBuilder(map.get(point.posY));
                sb.setCharAt(point.posX, 'G');
                map.set(point.posY, sb.toString());
            }
        }
        StringBuilder rmBombs = new StringBuilder(map.get(request.posY));
        rmBombs.setCharAt(request.posX, 'G');
        map.set(request.posY, rmBombs.toString());
        gameData.modifyGameMap(gameId, mapToString(map));
        this.killReachablePlayers(this.gameModelToEntity(gameData.findGame(gameId)),
                                    request);
        this.finishGame(gameId);
        EntPlayer player = playerService.playerModelToEntity(playerData.findPlayer(playerId));
        player.canPlaceBombs = true;
        playerData.modifyPlayer(player);
    }

    public void killReachablePlayers(EntGameEnt game, PutBombRequest request){
        for(var p: game.players){
            MovePlayerRequest pRequest = new MovePlayerRequest(request.posX, request.posY);
            if (playerService.isDirectionCardinal(pRequest, p.id) ||
                    (p.posX == request.posX && p.posY == request.posY)){
                p.lives = p.lives - 1;
                p.posX = p.initX;
                p.posY = p.initY;
                playerData.modifyPlayer(p);
            }
        }
    }

}
