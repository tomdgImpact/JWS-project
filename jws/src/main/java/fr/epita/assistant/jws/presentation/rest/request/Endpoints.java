package fr.epita.assistant.jws.presentation.rest.request;

import fr.epita.assistant.jws.domain.entity.EntGameEnt;
import fr.epita.assistant.jws.domain.service.GameService;
import fr.epita.assistant.jws.domain.service.MapProcessing;
import fr.epita.assistant.jws.domain.service.PlayerService;
import fr.epita.assistant.jws.presentation.rest.response.GameDetailResponse;
import fr.epita.assistant.jws.presentation.rest.response.PlayerDTO;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static fr.epita.assistant.jws.presentation.rest.response.GameState.FINISHED;
import static fr.epita.assistant.jws.presentation.rest.response.GameState.RUNNING;

@Path("/games")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Endpoints {

    @ConfigProperty(name ="JWS_MAP_PATH") String mapPath;
    @ConfigProperty(name ="JWS_TICK_DURATION") int tickDuration;
    @ConfigProperty(name ="JWS_DELAY_MOVEMENT") int delayMovement;
    @ConfigProperty(name ="JWS_DELAY_BOMB") int delayBomb;
    @ConfigProperty(name ="JWS_DELAY_FREE") int delayFree;
    @Inject PlayerService playerSerivce;
    @Inject GameService gameService;


    @GET
    @Path("/")
    public Response getAllGames(){
        var result = gameService.gameListResponse().games;
        return Response.ok(result).build();
    }

    @POST
    @Path("/")
    public Response postCreateNewGame(CreateGameRequest request) throws Exception {
        if (request == null || request.name == null){
            return Response.status(400).build();
        }
        List<String> map = MapProcessing.getArrayMap(mapPath);
        String strMap = String.join("", map);
        EntGameEnt EntGameEnt = gameService.createGame(strMap, request.name);
        GameDetailResponse res = gameService.entityToDetailResponse(EntGameEnt);
        return Response.ok(res).build();
    }

    @GET
    @Path("/{gameId}")
    public Response getGameInfo(@PathParam("gameId") long gameId) {
        GameDetailResponse res = gameService.getGameInfo(gameId);
        if (res == null){
            return Response.status(404).build();
        }
        return Response.ok(res).build();
    }

    @POST
    @Path("/{gameId}")
    public Response postJoinGame(@PathParam("gameId") long gameId,
                                                  CreateGameRequest request){
        GameDetailResponse gameInfo = gameService.getGameInfo(gameId);
        if (gameInfo == null){
            return Response.status(404).build();
        }
        if (request == null || request.name == null || gameInfo.players.size() == 4 ||
            gameInfo.state.equals("RUNNING") || gameInfo.state.equals("FINISHED")) {
            return Response.status(400).build();
        }
        gameService.joinGame(gameId, request.name);
        GameDetailResponse res = gameService.getGameInfo(gameId);
        return Response.ok(res).build();
    }

    @PATCH
    @Path("/{gameId}/start")
    public Response patchStartGame(@PathParam("gameId") int gameId){
        GameDetailResponse gameInfo = gameService.getGameInfo(gameId);
        if (gameInfo == null){
            return Response.status(404).build();
        }
        if (gameInfo.players.size() == 1){
            gameService.modifyGame(FINISHED, gameId);
            return Response.ok(gameService.getGameInfo(gameId)).build();
        }
        if (!gameInfo.state.equals("STARTING")){
            return Response.status(404).build();
        }
        gameService.modifyGame(RUNNING, gameId);
        gameInfo = gameService.getGameInfo(gameId);
        return Response.ok(gameInfo).build();
    }


    @POST
    @Path("/{gameId}/players/{playerId}/bomb")
    public Response postPutBomb(@PathParam("gameId") int gameId,
                                                 @PathParam("playerId") int playerId,
                                                 PutBombRequest request){
        if (request == null){
            return Response.status(400).build();
        }
        GameDetailResponse game = gameService.getGameInfo(gameId);
        if (game == null || !playerSerivce.isPlayerInGame(game, playerId)){
            return Response.status(404).build();
        }
        if (!game.state.equals("RUNNING") || playerSerivce.isPlayerDead(playerId) ||
                playerSerivce.findPlayerinGame(game, playerId).posX != request.posX ||
                playerSerivce.findPlayerinGame(game, playerId).posY != request.posY){
            return Response.status(400).build();
        }
        if (!playerSerivce.canPlayerPlaceBomb(playerId, tickDuration, delayBomb, request)){
            return Response.status(429).build();
        }
        gameService.putBomb(gameId, request, playerId);
        CompletableFuture.supplyAsync( () -> {
            gameService.explodeBomb(gameId, tickDuration, delayBomb, request, playerId);
            return null;
        });
        return Response.ok(gameService.getGameInfo(gameId)).build();
    }

    @POST
    @Path("/{gameId}/players/{playerId}/move")
    public Response postMovePlayer(@PathParam("gameId") long gameId,
                                   @PathParam("playerId") long playerId,
                                   MovePlayerRequest request){

        GameDetailResponse gameInfo = gameService.getGameInfo(gameId);
        if (gameInfo == null){
            return Response.status(404).build();
        }
        PlayerDTO player = playerSerivce.findPlayerinGame(gameInfo, playerId);
        if (player == null){
            return Response.status(404).build();
        }
        if (gameInfo.state.equals("FINISHED") || gameInfo.state.equals("STARTING") ||
            playerSerivce.isPlayerDead(playerId) || request == null ||
            !gameService.isValidPoint(request.posX, request.posY, gameId) ||
            !playerSerivce.isDirectionCardinal(request, playerId)){
            return Response.status(400).build();
        }
        if (!playerSerivce.canPlayerMove(playerId, tickDuration, delayMovement)){
            return Response.status(429).build();
        }
        playerSerivce.movePlayer(request.posX, request.posY, playerId);
        GameDetailResponse res = gameService.getGameInfo(gameId);
        return Response.ok(res).build();
    }
}