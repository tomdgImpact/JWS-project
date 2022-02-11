package fr.epita.assistant.jws.presentation.rest.response;

import lombok.Value;
import lombok.With;

import java.util.HashSet;

@Value @With
public class GameListResponse {
    public HashSet<GameResponse> games = new HashSet<>();

    public void addGame(GameResponse game){
        this.games.add(game);
    }

    public GameListResponse(){
    }
}
